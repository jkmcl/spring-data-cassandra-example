package jkml.data.repository;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jkml.data.entity.ScheduledTask;
import jkml.data.entity.TaskLock;

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
		TaskLock taskLock = repo.findById(name).get();
		log.info("Name: {}; Owner: {}; AcquireTs: {}",
				taskLock.getName(), taskLock.getOwner(), taskLock.getAcquireTs());
	}

	public void logScheduledTaskState(String name) {
		logTaskLockState(name);
		ScheduledTask schedTask = schedTaskRepo.findById(name).get();
		log.info("Name: {}; LastStartTs: {}; LastEndTs: {}",
				schedTask.getName(), formtTimestamp(schedTask.getLastStartTs()), formtTimestamp(schedTask.getLastEndTs()));
	}

}
