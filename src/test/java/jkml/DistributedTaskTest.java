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

import jkml.data.TaskLock;
import jkml.data.TaskLockRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners(mergeMode=MergeMode.MERGE_WITH_DEFAULTS, listeners=CassandraUnitDependencyInjectionIntegrationTestExecutionListener.class)
@CassandraDataSet(keyspace="mykeyspace", value={ "ddl.cql" })
@EmbeddedCassandra
public class DistributedTaskTest {

	private final Logger log = LoggerFactory.getLogger(DistributedTaskTest.class);

	@Autowired
	private TaskLockRepository taskLockRepo;

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

		log.info("Creating distributed task...");
		DistributedTask distTask = new DistributedTask(taskLockRepo, taskName, task);

		log.info("Running distributed task...");
		distTask.run();
	}

}
