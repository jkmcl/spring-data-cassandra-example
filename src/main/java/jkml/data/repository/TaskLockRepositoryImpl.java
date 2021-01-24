package jkml.data.repository;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.WriteTimeoutException;

import jkml.data.entity.TaskLock;

public class TaskLockRepositoryImpl implements TaskLockRepositoryCustom {

	private static final String UPDATE_QUERY_LOCK_ENABLED = "UPDATE task_lock SET owner = ?, acquire_ts = ? WHERE name = ? IF owner = ?";

	private static final String UPDATE_QUERY_LOCK_DISABLED = "UPDATE task_lock SET owner = ?, acquire_ts = ? WHERE name = ?";

	private static final String SELECT_QUERY = "SELECT owner, acquire_ts, timeout FROM task_lock WHERE name = ?";

	private final Logger log = LoggerFactory.getLogger(TaskLockRepositoryImpl.class);

	private PreparedStatement updateStmt;
	private PreparedStatement selectStmt;

	@Autowired
	private Session session;

	@Value("${app.lockingEnabled:true}")
	private boolean lockingEnabled;

	private PreparedStatement prepare(String query) {
		log.info("Creating prepared statement: {}", query);
		return session.prepare(query);
	}

	@PostConstruct
	public void init() {
		log.info("Locking enabled: {}", lockingEnabled);
		updateStmt = prepare(lockingEnabled ? UPDATE_QUERY_LOCK_ENABLED : UPDATE_QUERY_LOCK_DISABLED);
		selectStmt = prepare(SELECT_QUERY);
	}

	@Override
	public TaskLock tryLock(String name) {
		UUID owner = UUID.randomUUID();

		// Try acquiring lock
		TaskLock lock = tryLock(name, owner, null);
		if (lock != null) {
			return lock;
		}

		// If not successful, attempt to take over the lock only if it is still acquired by someone else and the timeout period has elapsed
		Row row = session.execute(selectStmt.bind(name)).one();
		int timeout = row.getInt("timeout");
		UUID currentOwner = row.getUUID("owner");
		Date currentAcquireTs = row.getTimestamp("acquire_ts");

		if (currentOwner == null || currentAcquireTs == null) {
			return null;
		}

		if (!currentAcquireTs.toInstant().plusSeconds(timeout).isBefore(Instant.now())) {
			return null;
		}

		log.info("Trying to take over lock acquired at {} over {} seconds ago: {}", currentAcquireTs.toInstant(), timeout, name);
		lock = tryLock(name, owner, currentOwner);
		log.info("Takeover of lock was {}: {}", lock == null ? "unsuccessful" : "successful", name);
		return lock;
	}

	private TaskLock tryLock(String name, UUID nextOwner, UUID currentOwner) {
		do {
			try {
				Date acquireTs = new Date();
				if (lockingEnabled && !session.execute(updateStmt.bind(nextOwner, acquireTs, name, currentOwner)).wasApplied()) {
					return null;
				} else if (!lockingEnabled) {
					session.execute(updateStmt.bind(nextOwner, acquireTs, name));
				}
				TaskLock lock = new TaskLock(name);
				lock.setOwner(nextOwner);
				lock.setAcquireTs(acquireTs);
				return lock;
			} catch (WriteTimeoutException e) {
				log.info("Write timeout during lock acquisition. Retyring...", e);
			}
		} while (true);
	}

	@Override
	public void unlock(TaskLock lock) {
		do {
			try {
				if (lockingEnabled && !session.execute(updateStmt.bind(null, null, lock.getName(), lock.getOwner())).wasApplied()) {
					throw new RuntimeException("Error releasing lock: " + lock.getName() + "; owner: " + lock.getOwner());
				} else if (!lockingEnabled) {
					session.execute(updateStmt.bind(null, null, lock.getName()));
				}
				return;
			} catch (WriteTimeoutException e) {
				log.info("Write timeout during lock release. Retyring...", e);
			}
		} while (true);
	}

	public boolean isLockingEnabled() {
		return lockingEnabled;
	}

}
