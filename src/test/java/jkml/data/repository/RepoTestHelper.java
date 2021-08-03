package jkml.data.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jkml.data.entity.TaskLock;
import jkml.data.entity.TaskSchedule;

@Component
public class RepoTestHelper {

	private final Logger log = LoggerFactory.getLogger(RepoTestHelper.class);

	@Autowired
	private TaskLockRepository repo;

	@Autowired
	private TaskScheduleRepository schedTaskRepo;

	public void logTaskLockState(String name) {
		TaskLock taskLock = repo.findById(name).get();
		log.info("Name: {}; Owner: {}; AcquireTs: {}",
				taskLock.getName(), taskLock.getOwner(), taskLock.getAcquireTs());
	}

	public void logScheduledTaskState(String name) {
		logTaskLockState(name);
		TaskSchedule schedTask = schedTaskRepo.findById(name).get();
		log.info("Name: {}; LastStartTs: {}; LastEndTs: {}",
				schedTask.getName(), schedTask.getLastStartTs(), schedTask.getLastEndTs());
	}

}
