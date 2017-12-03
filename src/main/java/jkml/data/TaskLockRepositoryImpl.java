package jkml.data;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.WriteTimeoutException;

public class TaskLockRepositoryImpl implements TaskLockRepositoryCustom {

	private static final String GENERAL_UPDATE_QUERY = "UPDATE task_lock SET owner = ?, acquire_ts = ? WHERE name = ? IF owner = ?";

	private static final String SPECIAL_UPDATE_QUERY = "UPDATE task_lock SET owner = null, acquire_ts = null WHERE name = ? IF owner = ? AND acquire_ts = ?";

	private static final String SELECT_QUERY = "SELECT owner, acquire_ts, timeout FROM task_lock WHERE name = ?";

	private final Logger log = LoggerFactory.getLogger(TaskLockRepositoryImpl.class);

	private PreparedStatement generalUpdateStmt;
	private PreparedStatement specialUpdateStmt;
	private PreparedStatement selectStmt;

	@Autowired
	private CassandraOperations operations;

	private PreparedStatement prepare(Session session, String query) {
		log.info("Creating prepared statement: {}", query);
		return session.prepare(query);
	}

	@PostConstruct
	public void init() {
		Session session = operations.getSession();
		generalUpdateStmt = prepare(session, GENERAL_UPDATE_QUERY);
		selectStmt = prepare(session, SELECT_QUERY);
		specialUpdateStmt = prepare(session, SPECIAL_UPDATE_QUERY);
	}

	private ResultSet executeWithoutWriteTimeout(Statement statement) {
		do {
			try {
				return operations.getSession().execute(statement);
			} catch (WriteTimeoutException e) {
				log.info("Write timeout but will retry", e);
			}
		} while (true);
	}

	/**
	 * Check if the lock has been acquired beyond the timeout period. If yes, attempt to unlock it.
	 * @return true if timeout occurred and unlock attempt has been made, false otherwise.
	 */
	private boolean timeoutElapsed(String name) {
		Row row = operations.getSession().execute(selectStmt.bind(name)).one();
		UUID owner = row.getUUID("owner");
		Date acquireTs = row.getTimestamp("acquire_ts");
		int timeout = row.getInt("timeout");

		if (owner == null || acquireTs == null) {
			return false;
		}

		if (!acquireTs.toInstant().plusSeconds(timeout).isBefore(Instant.now())) {
			return false;
		}

		// Attempt to release the lock
		log.info("Lock acquired at {} and held over {} seconds. Trying to release it: {}", acquireTs.toInstant(), timeout, name);
		if (executeWithoutWriteTimeout(specialUpdateStmt.bind(name, owner, acquireTs)).wasApplied()) {
			log.info("Lock released: {}", name);
		}
		else {
			log.info("Lock not released: {}", name);
		}
		return true;
	}

	@Override
	public TaskLock tryLock(String name) {
		// Try to acquire the lock and return if successful
		UUID owner = UUID.randomUUID();
		TaskLock lock = new TaskLock();
		lock.setName(name);
		if (executeWithoutWriteTimeout(generalUpdateStmt.bind(owner, new Date(), name, null)).wasApplied()) {
			lock.setOwner(owner);
			return lock;
		}

		// Otherwise, check if the lock has been held beyond the timeout
		if (!timeoutElapsed(name)) {
			lock.setOwner(null);
			return lock;
		}

		lock.setOwner(executeWithoutWriteTimeout(generalUpdateStmt.bind(owner, new Date(), name, null)).wasApplied() ? owner : null);
		return lock;
	}

	@Override
	public void unlock(TaskLock lock) {
		if (!executeWithoutWriteTimeout(generalUpdateStmt.bind(null, null, lock.getName(), lock.getOwner())).wasApplied()) {
			throw new RuntimeException("Error releasing lock: " + lock.getName() + "; owner: " + lock.getOwner());
		}
	}

}
