package jkml.data;

import java.time.Instant;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class TaskLockRepositoryImpl implements TaskLockRepositoryCustom {

	private static final String GENERAL_UPDATE_QUERY = "UPDATE task_lock SET acquired = ?, acquire_ts = ? WHERE name = ? IF acquired = ?";

	private static final String SELECT_QUERY = "SELECT timeout, acquire_ts FROM task_lock WHERE name = ?";

	private static final String SPECIAL_UPDATE_QUERY = "UPDATE task_lock SET acquired = false, acquire_ts = null WHERE name = ? IF acquired = true AND acquire_ts = ?";

	private final Logger log = LoggerFactory.getLogger(TaskLockRepositoryImpl.class);

	private PreparedStatement generalUpdateStmt;
	private PreparedStatement selectStmt;
	private PreparedStatement specialUpdateStmt;

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

	@Override
	public boolean tryLock(String name) {
		Session session = operations.getSession();

		// Try to acquire the lock and return if successful
		if (session.execute(generalUpdateStmt.bind(true, new Date(), name, false)).wasApplied()) {
			return true;
		}

		// Check if the lock has been held beyond the max duration
		Row row = session.execute(selectStmt.bind(name)).one();
		int timeout = row.getInt("timeout");
		Date acquireTs = row.getTimestamp("acquire_ts");

		// If no, do nothing
		if (!acquireTs.toInstant().plusSeconds(timeout).isBefore(Instant.now())) {
			return false;
		}

		// Otherwise, release the lock and then try acquire again
		log.info("Lock acquired at {} and held over {} seconds. Trying to release it: {}", acquireTs.toInstant(), timeout, name);
		if (session.execute(specialUpdateStmt.bind(name, acquireTs)).wasApplied()) {
			log.info("Lock released: {}", name);
		}
		else {
			log.info("Lock not released: {}", name);
		}

		return session.execute(generalUpdateStmt.bind(true, new Date(), name, false)).wasApplied();
	}

	@Override
	public void unlock(String name) {
		Session session = operations.getSession();
		if (!session.execute(generalUpdateStmt.bind(false, null, name, true)).wasApplied()) {
			throw new RuntimeException("Error releasing lock: " + name);
		}
	}

}
