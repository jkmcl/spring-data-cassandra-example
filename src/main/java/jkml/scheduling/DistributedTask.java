package jkml.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jkml.data.TaskLock;
import jkml.data.TaskLockRepository;

/**
 * This is a wrapper of {@link Runnable}. When the {@link Runnable#run()} method of the underlying {@link Runnable}
 * instance is executed by multiple concurrent threads, only one of the threads will be able to acquire the lock of the
 * task and allowed to execute the method.
 * <p>Use case: multiple application instances schedule the same fixed-delay task to be started but only one instance
 * of the task should run at any time.
 */
public class DistributedTask implements Runnable {

	private final Logger log = LoggerFactory.getLogger(DistributedTask.class);

	private final TaskLockRepository taskLockRepo;

	protected final String taskName;

	protected final Runnable task;

	public DistributedTask(TaskLockRepository taskLockRepo, String taskName, Runnable task) {
		this.taskLockRepo = taskLockRepo;
		this.taskName = taskName;
		this.task = task;
	}

	protected void executeTask() {
		log.debug("Executing task: {}", taskName);
		task.run();
	}

	@Override
	public void run() {
		// Acquire lock
		log.debug("Trying to acquire task lock: {}", taskName);
		TaskLock lock = taskLockRepo.tryLock(taskName);
		if (lock == null) {
			log.debug("Unable to acquire task lock: {}", taskName);
			return;
		}

		// Run underlying task and release lock when done
		try {
			log.debug("Acquired task lock: {}", taskName);
			executeTask();
		}
		finally {
			log.debug("Releasing task lock: {}", taskName);
			taskLockRepo.unlock(lock);
		}
	}

}
