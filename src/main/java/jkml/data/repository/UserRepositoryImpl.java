package jkml.data.repository;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.AsyncCassandraOperations;
import org.springframework.util.concurrent.ListenableFutureCallback;

import jkml.data.entity.User;

public class UserRepositoryImpl implements UserRepositoryCustom {

	private final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);

	@Autowired
	private AsyncCassandraOperations operations;

	@Override
	public void ingest(List<User> users) {

		AtomicBoolean hasError = new AtomicBoolean(false);

		// Control number of in-flight async inserts using a semaphore
		int numPermits = 1024; // Default per connection current request limit in DataStax driver
		Semaphore semaphore = new Semaphore(numPermits);

		for (User user : users) {

			semaphore.acquireUninterruptibly();
			if (hasError.get()) {
				semaphore.release();
				break;
			}

			operations.insert(user).addCallback(new ListenableFutureCallback<User>() {

				@Override
				public void onSuccess(User result) {
					log.debug("Where am I?");
					semaphore.release();
				}

				@Override
				public void onFailure(Throwable ex) {
					log.error("Error executing asynchronous insertion", ex);
					hasError.set(false);
					semaphore.release();
				}

			});
		}

		// Wait for all inserts to complete
		semaphore.acquireUninterruptibly(numPermits);

		if (hasError.get()) {
			throw new RuntimeException("Error executing one or more asynchronous insertions");
		}

	}

}
