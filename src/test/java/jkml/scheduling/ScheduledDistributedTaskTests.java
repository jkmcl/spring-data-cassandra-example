package jkml.scheduling;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.cassandraunit.spring.CassandraDataSet;
import org.cassandraunit.spring.CassandraUnitDependencyInjectionIntegrationTestExecutionListener;
import org.cassandraunit.spring.EmbeddedCassandra;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;

import jkml.data.entity.TaskLock;
import jkml.data.entity.TaskSchedule;
import jkml.data.repository.RepoTestHelper;
import jkml.data.repository.TaskLockRepository;
import jkml.data.repository.TaskScheduleRepository;

@SpringBootTest
@TestExecutionListeners(mergeMode=MergeMode.MERGE_WITH_DEFAULTS, listeners=CassandraUnitDependencyInjectionIntegrationTestExecutionListener.class)
@CassandraDataSet(keyspace="keyspace1", value={ "schema.cql" })
@EmbeddedCassandra
class ScheduledDistributedTaskTests {

	private final Logger log = LoggerFactory.getLogger(ScheduledDistributedTaskTests.class);

	@Autowired
	private TaskLockRepository taskLockRepo;

	@Autowired
	private TaskScheduleRepository schedTaskRepo;

	@Autowired
	private RepoTestHelper testHelper;

	@Test
	void test() throws Exception {
		log.info("Creating task lock entity...");
		String taskName = MyTask.class.getSimpleName();
		TaskLock taskLock = new TaskLock();
		taskLock.setName(taskName);
		taskLock.setTimeout(10);
		taskLockRepo.save(taskLock);

		log.info("Creating scheduled task entity...");
		int maxTsOffset = 5;
		TaskSchedule schedTask = new TaskSchedule();
		schedTask.setName(taskName);
		schedTask.setMaxTsOffset(maxTsOffset);
		schedTaskRepo.save(schedTask);

		log.info("Creating scheduled distributed task...");
		MyTask task = new MyTask();
		ScheduledDistributedTask distTask = new ScheduledDistributedTask(schedTaskRepo, taskLockRepo, taskName, task);

		testHelper.logScheduledTaskState(taskName);

		log.info("Running scheduled distributed task...");
		distTask.run();
		testHelper.logScheduledTaskState(taskName);
		assertTrue(task.isExecuted());
		task.setExecuted(false);

		log.info("Running scheduled distributed task again immediately...");
		distTask.run();
		testHelper.logScheduledTaskState(taskName);
		assertTrue(!task.isExecuted());

		log.info("Running scheduled distributed task again after timeout period ...");
		await().pollDelay(Duration.ofSeconds(maxTsOffset + 1)).until(() -> true);
		distTask.run();
		testHelper.logScheduledTaskState(taskName);
		assertTrue(task.isExecuted());
	}

}
