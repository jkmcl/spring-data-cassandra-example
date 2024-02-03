package jkml.data.core;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.core.AsyncCassandraOperations;

public class CustomCassandraOperations {

	private static final int PERMITS = 1024; // DataStax driver's default max number of concurrent requests per connection

	private final Logger log = LoggerFactory.getLogger(CustomCassandraOperations.class);

	private final AsyncCassandraOperations asyncOperations;

	public CustomCassandraOperations(AsyncCassandraOperations asyncOperations) {
		this.asyncOperations = asyncOperations;
	}

	public <T>long ingest(Iterable<T> entities) {

		AtomicBoolean hasError = new AtomicBoolean(false);
		AtomicLong count = new AtomicLong(0);

		// Control number of in-flight async inserts using a semaphore
		Semaphore semaphore = new Semaphore(PERMITS);

		for (T entity : entities) {

			semaphore.acquireUninterruptibly();
			if (hasError.get()) {
				semaphore.release();
				break;
			}

			try {
				asyncOperations.insert(entity).whenComplete((e, t) -> {
					if (t == null) {
						count.incrementAndGet();
					} else {
						log.error("Error executing asynchronous insertion", t);
						hasError.set(true);
					}
					semaphore.release();
				});
			} catch (Exception e) {
				log.error("Error initiating asynchronous insertion", e);
				hasError.set(true);
				semaphore.release();
			}

		}

		// Wait for all inserts to complete
		semaphore.acquireUninterruptibly(PERMITS);

		return count.get();
	}

}
