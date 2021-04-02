package jkml.data.repository;

import java.time.Instant;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.servererrors.WriteTimeoutException;

import jkml.data.entity.TaskLock;

public class TaskLockRepositoryImpl implements TaskLockRepositoryCustom {

	private static final String OWNER_COLUMN = "owner";

	private static final String TIMEOUT_COLUMN = "timeout";

	private static final String ACQUIRE_TS_COLUMN = "acquire_ts";

	private static final String UPDATE_QUERY = "UPDATE task_lock SET owner = ?, acquire_ts = ? WHERE name = ? IF owner = ?";

	private static final String SELECT_QUERY = "SELECT owner, acquire_ts, timeout FROM task_lock WHERE name = ?";

	private final Logger log = LoggerFactory.getLogger(TaskLockRepositoryImpl.class);

	private PreparedStatement updateStmt;

	private PreparedStatement selectStmt;

	@Autowired
	private CqlSession session;

	private PreparedStatement prepare(String query) {
		log.info("Creating prepared statement: {}", query);
		return session.prepare(query);
	}

	@PostConstruct
	private void postConstruct() {
		updateStmt = prepare(UPDATE_QUERY);
		selectStmt = prepare(SELECT_QUERY);
	}

	@Override
	public TaskLock tryLock(String name) {
		UUID nextOwner = UUID.randomUUID();

		// Try acquiring lock
		Instant acquireTs = tryLock(name, nextOwner, null);
		if (acquireTs != null) {
			TaskLock lock = new TaskLock(name);
			lock.setOwner(nextOwner);
			lock.setAcquireTs(acquireTs);
			return lock;
		}

		// If not successful, attempt to take over the lock only if it is still acquired by someone else and the timeout period has elapsed
		Row row = session.execute(selectStmt.bind(name)).one();
		int timeout = row.getInt(TIMEOUT_COLUMN);
		UUID actualOwner = row.getUuid(OWNER_COLUMN);
		Instant actualAcquireTs = row.getInstant(ACQUIRE_TS_COLUMN);

		if (actualOwner == null || actualAcquireTs == null) {
			return null;
		}

		if (!actualAcquireTs.plusSeconds(timeout).isBefore(Instant.now())) {
			return null;
		}

		log.info("Trying to take over lock acquired by {} at {} over {} seconds ago: {}", actualOwner, actualAcquireTs, timeout, name);
		acquireTs = tryLock(name, nextOwner, actualOwner);
		if (acquireTs != null) {
			log.info("Lock takeover succeeded");
			TaskLock lock = new TaskLock(name);
			lock.setOwner(nextOwner);
			lock.setAcquireTs(acquireTs);
			return lock;
		}

		log.info("Lock takeover failed");
		return null;
	}

	private Instant isOwner(String name, UUID owner) {
		Row row = session.execute(selectStmt.bind(name)).one();
		UUID actualOwner = row.getUuid(OWNER_COLUMN);
		return (actualOwner != null && actualOwner.equals(owner)) ? row.getInstant(ACQUIRE_TS_COLUMN) : null;
	}

	private boolean isNotOwner(String name, UUID owner) {
		UUID actualOwner = session.execute(selectStmt.bind(name)).one().getUuid(OWNER_COLUMN);
		return actualOwner == null || !actualOwner.equals(owner);
	}

	private Instant tryLock(String name, UUID nextOwner, UUID currentOwner) {
		boolean retry = false;
		do {
			Instant acquireTs = Instant.now();
			try {
				if (session.execute(updateStmt.bind(nextOwner, acquireTs, name, currentOwner)).wasApplied()) {
					return acquireTs;
				}

				// Check if a previous conditional update was finally applied
				if (retry) {
					return isOwner(name, nextOwner);
				}

				return null;
			} catch (WriteTimeoutException e) {
				log.info("Write timeout during lock acquisition: {}; current owner: {}; next owner: {}", name, currentOwner, nextOwner);

				// Check if conditional update was applied
				if ((acquireTs = isOwner(name, nextOwner)) != null) {
					log.info("Lock acquisition was successful");
					return acquireTs;
				}

				// Retry if not
				log.info("Retry lock acquisition");
				retry = true;
			}
		} while (true);
	}

	@Override
	public void unlock(TaskLock lock) {
		String name = lock.getName();
		UUID currentOwner = lock.getOwner();
		do {
			try {
				session.execute(updateStmt.bind(null, null, name, currentOwner));
				return;
			} catch (WriteTimeoutException e) {
				log.info("Write timeout during lock release: {}; current owner: {}", name, currentOwner);

				// Check if conditional update was applied
				if (isNotOwner(name, currentOwner)) {
					log.info("Lock release was successful");
					return;
				}

				// Retry if not
				log.info("Retry lock release");
			}
		} while (true);
	}

}
