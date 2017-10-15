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

public class ScheduledTaskRepositoryImpl implements ScheduledTaskRepositoryCustom {

	private static final String GENERAL_UPDATE_QUERY = "UPDATE scheduled_task SET locked = ?, lock_time = ? WHERE name = ? IF locked = ?";

	private static final String SELECT_QUERY = "SELECT lock_time, max_lock_duration FROM scheduled_task WHERE name = ?";

	private static final String SPECIAL_UPDATE_QUERY = "UPDATE scheduled_task SET locked = false, lock_time = null WHERE name = ? IF locked = true AND lock_time = ?";

	private final Logger log = LoggerFactory.getLogger(ScheduledTaskRepositoryImpl.class);

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
		Date lockTime = row.getTimestamp("lock_time");
		int maxLockDuration = row.getInt("max_lock_duration");

		// If no, do nothing
		if (!lockTime.toInstant().plusSeconds(maxLockDuration).isBefore(Instant.now())) {
			return false;
		}

		// Otherwise, release the lock and then try acquire again
		log.info("Lock acquired at {} and held over {} seconds. Trying to release it: {}", lockTime, maxLockDuration, name);
		if (session.execute(specialUpdateStmt.bind(name, lockTime)).wasApplied()) {
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
