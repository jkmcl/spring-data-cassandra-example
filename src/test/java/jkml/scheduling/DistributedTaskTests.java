package jkml.scheduling;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
import jkml.data.repository.TaskLockRepository;

@SpringBootTest
@TestExecutionListeners(mergeMode=MergeMode.MERGE_WITH_DEFAULTS, listeners=CassandraUnitDependencyInjectionIntegrationTestExecutionListener.class)
@CassandraDataSet(keyspace="keyspace1", value={ "schema.cql" })
@EmbeddedCassandra
class DistributedTaskTests {

	private final Logger log = LoggerFactory.getLogger(DistributedTaskTests.class);

	@Autowired
	private TaskLockRepository taskLockRepo;

	@Test
	void test() {
		log.info("Running plain task...");
		MyTask task = new MyTask();
		task.run();
		assertTrue(task.isExecuted());

		log.info("Creating task lock entity...");
		String taskName = MyTask.class.getSimpleName();
		TaskLock taskLock = new TaskLock();
		taskLock.setName(taskName);
		taskLock.setTimeout(10);
		taskLockRepo.save(taskLock);

		log.info("Creating distributed task...");
		task.setExecuted(false);
		DistributedTask distTask = new DistributedTask(taskLockRepo, taskName, task);

		log.info("Running distributed task...");
		distTask.run();
		assertTrue(task.isExecuted());
	}

}
