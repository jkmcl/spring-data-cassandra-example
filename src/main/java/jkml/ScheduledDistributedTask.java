package jkml;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jkml.data.ScheduledTask;
import jkml.data.ScheduledTaskRepository;
import jkml.data.TaskLockRepository;

/**
 * This is a wrapper of {@link Runnable}. When the {@link Runnable#run()} method of the underlying {@link Runnable}
 * instance is executed by multiple concurrent threads, only one of the threads will be able to acquire the lock of the
 * task and allowed to execute the method. In addition, the previous start time of the task is compared against the
 * current time. If the duration is within the maximum offset, the task is considered already started and will not
 * be executed.
 * <p>Use case: multiple application instances schedule the same task to be started at a specific time but only one
 * instance of the task should at that time. In addition, the most recent start time of the task is checked against a
 * maximum offset and the current time to determine if the task has started. This prevents the same day-end batch to be
 * executed twice, for example.
 */
public class ScheduledDistributedTask extends DistributedTask {

	private final Logger log = LoggerFactory.getLogger(ScheduledDistributedTask.class);

	protected final ScheduledTaskRepository schedTaskRepo;

	public ScheduledDistributedTask(
			ScheduledTaskRepository schedTaskRepo, TaskLockRepository taskLockRepo, String taskName, Runnable task) {
		super(taskLockRepo, taskName, task);
		this.schedTaskRepo = schedTaskRepo;
	}

	/**
	 * Check if this instance of the task has been started, i.e. within the offset of the last start time.
	 */
	private boolean isStarted(ScheduledTask schedTask) {
		String name = schedTask.getName();

		// Check if task was started previously
		Date lastStartTs = schedTask.getLastStartTs();
		if (lastStartTs == null) {
			log.debug("Task ({}) was not started previously", name);
			return false;
		}

		log.debug("Task ({}) was most recently started at {}", name, lastStartTs.toInstant());

		// Check if task was started a long time ago (more than the max offset)
		long maxTsOffset = schedTask.getMaxTsOffset();
		Duration maxOffsetDuration = Duration.ofSeconds(maxTsOffset).abs();
		Duration offsetDuration = Duration.between(Instant.now(), lastStartTs.toInstant()).abs();

		if (offsetDuration.compareTo(maxOffsetDuration) > 0) {
			log.debug("This instance of the task ({}) is considered not started as the most recent start time is more than {} seconds ago",
					name, maxTsOffset);
			return false;
		}

		log.debug("This instance of the task ({}) is considered started as the most recent start time is less than or equal to {} seconds ago",
				name, maxTsOffset);
		return true;
	}

	@Override
	public void run() {
		// Acquire lock
		log.debug("Trying to acquire task lock: {}", taskName);
		if (!taskLockRepo.tryLock(taskName)) {
			log.debug("Unable to acquire task lock: {}", taskName);
			return;
		}

		// Run underlying task and release lock when done
		try {
			log.debug("Acquired task lock: {}", taskName);
			ScheduledTask schedTask = schedTaskRepo.findOne(taskName);
			if (isStarted(schedTask)) {
				log.debug("Skip task execution as it has already been started: {}", taskName);
				return;
			}
			log.debug("Executing task: {}", taskName);
			schedTask.setLastStartTs(new Date());
			schedTask.setLastEndTs(null);
			schedTask = schedTaskRepo.save(schedTask);
			task.run();
			schedTask.setLastEndTs(new Date());
			schedTaskRepo.save(schedTask);
		}
		finally {
			log.debug("Releasing task lock: {}", taskName);
			taskLockRepo.unlock(taskName);
		}
	}

}
