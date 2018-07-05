package jkml.data;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;

public class UserRepositoryImpl implements UserRepositoryCustom {

	private static final String INSERT_CQL = "INSERT INTO user (id, first_name, last_name) VALUES (?, ?, ?)";

	private static PreparedStatement insertStmt;

	private final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);

	@Autowired
	private Session session;

	@PostConstruct
	private void init() {
		log.info("Creating prepared statement: {}", INSERT_CQL);
		insertStmt = session.prepare(INSERT_CQL);
	}

	@Override
	public void ingest(List<User> users) {

		AtomicBoolean hasError = new AtomicBoolean(false);

		// Control number of in-flight async inserts using a semaphore
		int numPermits = session.getCluster().getConfiguration().getPoolingOptions().getMaxConnectionsPerHost(HostDistance.LOCAL);
		Semaphore semaphore = new Semaphore(numPermits);

		for (User user : users) {

			semaphore.acquireUninterruptibly();
			if (hasError.get()) {
				semaphore.release();
				break;
			}

			ResultSetFuture rsf = session.executeAsync(insertStmt.bind(user.getId(), user.getFirstName(), user.getLastName()));
			try {
				rsf.getUninterruptibly();
			} catch (Exception e) {
				log.error("Error executing asynchronous insertion", e);
				hasError.set(true);
			} finally {
				semaphore.release();
			}
		}

		// Wait for all inserts to complete
		semaphore.acquireUninterruptibly(numPermits);

		if (hasError.get()) {
			throw new RuntimeException("Error executing one or more asynchronous insertions");
		}

	}

}
