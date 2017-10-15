package jkml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jkml.data.ScheduledTaskRepository;

/**
 * This is a wrapper of {@link Runnable}. When the {@link Runnable#run()} method of the underlying {@link Runnable}
 * instance is executed by multiple concurrent threads, only one of the threads will be able to acquire the lock of the
 * task and allowed to execute the method.
 */
public class DistributedRunnable implements Runnable {

	private final Logger log = LoggerFactory.getLogger(DistributedRunnable.class);

	protected final ScheduledTaskRepository taskRepo;

	protected final String taskName;

	protected final Runnable task;

	public DistributedRunnable(ScheduledTaskRepository taskRepo, String taskName, Runnable task) {
		this.taskRepo = taskRepo;
		this.taskName = taskName;
		this.task = task;
	}

	@Override
	public void run() {
		// Acquire lock
		log.debug("Trying to acquire task lock: {}", taskName);
		if (!taskRepo.tryLock(taskName)) {
			log.debug("Unable to acquire task lock: {}", taskName);
			return;
		}

		// Run underlying task and release lock when done
		try {
			log.debug("Acquired task lock. Executing task: {}", taskName);
			task.run();
		}
		finally {
			log.debug("Releasing task lock: {}", taskName);
			taskRepo.unlock(taskName);
		}

	}

}
