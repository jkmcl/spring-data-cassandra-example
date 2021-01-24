package jkml.data.repository;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.AsyncCassandraOperations;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.Session;

import jkml.data.entity.User;

public class UserRepositoryImpl implements UserRepositoryCustom {

	private final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);

	@Autowired
	private Session session;

	@Autowired
	private AsyncCassandraOperations operations;

	private int numPermits = 1024;

	@PostConstruct
	public void init() {
		numPermits = session.getCluster().getConfiguration().getPoolingOptions().getMaxRequestsPerConnection(HostDistance.LOCAL);
	}

	@Override
	public void ingest(List<User> users) {

		AtomicBoolean hasError = new AtomicBoolean(false);

		// Control number of in-flight async inserts using a semaphore
		Semaphore semaphore = new Semaphore(numPermits);

		for (User user : users) {

			semaphore.acquireUninterruptibly();
			if (hasError.get()) {
				semaphore.release();
				break;
			}

			try {
				operations.insert(user).addCallback(new ListenableFutureCallback<User>() {

					@Override
					public void onSuccess(User result) {
						semaphore.release();
					}

					@Override
					public void onFailure(Throwable ex) {
						log.error("Error executing asynchronous insertion", ex);
						hasError.set(true);
						semaphore.release();
					}

				});
			} catch (Exception e) {
				log.error("Error initiating asynchronous insertion", e);
				hasError.set(true);
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
