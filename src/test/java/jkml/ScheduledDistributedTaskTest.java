package jkml;

import org.cassandraunit.spring.CassandraDataSet;
import org.cassandraunit.spring.CassandraUnitDependencyInjectionIntegrationTestExecutionListener;
import org.cassandraunit.spring.EmbeddedCassandra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit4.SpringRunner;

import jkml.data.RepoTestHelper;
import jkml.data.ScheduledTask;
import jkml.data.ScheduledTaskRepository;
import jkml.data.TaskLock;
import jkml.data.TaskLockRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners(mergeMode=MergeMode.MERGE_WITH_DEFAULTS, listeners=CassandraUnitDependencyInjectionIntegrationTestExecutionListener.class)
@CassandraDataSet(keyspace="mykeyspace", value={ "ddl.cql" })
@EmbeddedCassandra
public class ScheduledDistributedTaskTest {

	private final Logger log = LoggerFactory.getLogger(ScheduledDistributedTaskTest.class);

	@Autowired
	private TaskLockRepository taskLockRepo;

	@Autowired
	private ScheduledTaskRepository schedTaskRepo;

	@Autowired
	private RepoTestHelper testHelper;

	@Test
	public void test() throws Exception {

		Runnable task = new Runnable() {
			@Override
			public void run() {
				System.out.println("Hello world!");
			}
		};

		log.info("Running plain task...");
		task.run();

		log.info("Creating task lock entity...");
		String taskName = "MyTask";
		TaskLock taskLock = new TaskLock();
		taskLock.setName(taskName);
		taskLock.setTimeout(10);
		taskLockRepo.save(taskLock);

		log.info("Creating scheduled task entity...");
		int maxTsOffset = 5;
		ScheduledTask schedTask = new ScheduledTask();
		schedTask.setName(taskName);
		schedTask.setMaxTsOffset(maxTsOffset);
		schedTaskRepo.save(schedTask);

		log.info("Creating scheduled distributed task...");
		ScheduledDistributedTask distTask = new ScheduledDistributedTask(schedTaskRepo, taskLockRepo, taskName, task);

		testHelper.logScheduledTaskState(taskName);

		log.info("Running scheduled distributed task...");
		distTask.run();
		testHelper.logScheduledTaskState(taskName);

		log.info("Running scheduled distributed task again...");
		distTask.run();
		testHelper.logScheduledTaskState(taskName);

		log.info("Sleep for {} seconds", maxTsOffset + 1);
		Thread.sleep((maxTsOffset + 1) * 1000);

		log.info("Running scheduled distributed task again...");
		distTask.run();
		testHelper.logScheduledTaskState(taskName);
	}

}
