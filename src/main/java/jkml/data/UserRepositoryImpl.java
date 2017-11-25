package jkml.data;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.CqlOperations;

import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PreparedStatement;

public class UserRepositoryImpl implements UserRepositoryCustom {

	private static final String INSERT_CQL = "INSERT INTO user (id, first_name, last_name) VALUES (?, ?, ?)";

	private static PreparedStatement insertStmt;

	@Autowired
	private CqlOperations cqlOperations;

	@PostConstruct
	private void init() {
		insertStmt = cqlOperations.getSession().prepare(INSERT_CQL);
	}

	@Override
	public void ingest(List<User> users) {

		AtomicBoolean hasError = new AtomicBoolean(false);

		// Control number of in-flight async inserts using a semaphore
		int numPermits = cqlOperations.getSession().getCluster().getConfiguration().getPoolingOptions().getMaxConnectionsPerHost(HostDistance.LOCAL);
		Semaphore semaphore = new Semaphore(numPermits);

		for (User user : users) {

			if (hasError.get()) {
				break;
			}
			semaphore.acquireUninterruptibly();
			if (hasError.get()) {
				semaphore.release();
				break;
			}

			cqlOperations.executeAsynchronously(insertStmt.bind(user.getId(), user.getFirstName(), user.getLastName()), rsf -> {
				try {
					rsf.getUninterruptibly();
				} catch (Exception e) {
					hasError.set(true);
				} finally {
					semaphore.release();
				}
			});
		}

		semaphore.acquireUninterruptibly(numPermits);

		if (hasError.get()) {
			throw new RuntimeException("Error executing asynchronous insertion");
		}

	}

}
