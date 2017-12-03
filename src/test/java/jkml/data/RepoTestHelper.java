package jkml.data;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepoTestHelper {

	private final Logger log = LoggerFactory.getLogger(RepoTestHelper.class);

	@Autowired
	private TaskLockRepository repo;

	@Autowired
	private ScheduledTaskRepository schedTaskRepo;

	private static String formtTimestamp(Date date) {
		if (date == null) {
			return null;
		}
		return date.toInstant().toString();
	}

	public void logTaskLockState(String name) {
		TaskLock taskLock = repo.findOne(name);
		log.info("Name: {}; Owner: {}; AcquireTs: {}",
				taskLock.getName(), taskLock.getOwner(), formtTimestamp(taskLock.getAcquireTs()));
	}

	public void logScheduledTaskState(String name) {
		logTaskLockState(name);
		ScheduledTask schedTask = schedTaskRepo.findOne(name);
		log.info("Name: {}; LastStartTs: {}; LastEndTs: {}",
				schedTask.getName(), formtTimestamp(schedTask.getLastStartTs()), formtTimestamp(schedTask.getLastEndTs()));
	}

}
