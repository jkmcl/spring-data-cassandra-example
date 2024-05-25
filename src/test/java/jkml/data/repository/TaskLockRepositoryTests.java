package jkml.data.repository;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

@SpringBootTest
@TestExecutionListeners(mergeMode=MergeMode.MERGE_WITH_DEFAULTS, listeners=CassandraUnitDependencyInjectionIntegrationTestExecutionListener.class)
@CassandraDataSet(keyspace="keyspace1", value={ "schema.cql" })
@EmbeddedCassandra
class TaskLockRepositoryTests {

	private final Logger log = LoggerFactory.getLogger(TaskLockRepositoryTests.class);

	@Autowired
	private TaskLockRepository repo;

	@Autowired
	private RepoTestHelper testHelper;

	@Test
	void test() {

		String name = "MyTask";
		int timeout = 5;

		log.info("Creating entity...");
		TaskLock taskLock = new TaskLock();
		taskLock.setName(name);
		taskLock.setTimeout(timeout);
		repo.save(taskLock);
		testHelper.logTaskLockState(name);

		// First lock attempt should succeed
		log.info("Acquiring lock...");
		TaskLock taskLock1 = repo.tryLock(name);
		assertNotNull(taskLock1);
		testHelper.logTaskLockState(name);

		// Second attempt should fail since the lock has not been released, unless task locking is disabled
		log.info("Acquiring lock...");
		TaskLock taskLock2 = repo.tryLock(name);
		assertNull(taskLock2);
		testHelper.logTaskLockState(name);

		// Release lock
		log.info("Releasing lock...");
		repo.unlock(taskLock1);
		testHelper.logTaskLockState(name);

		// Third attempt should succeed
		log.info("Acquiring lock...");
		TaskLock taskLock3 = repo.tryLock(name);
		assertNotNull(taskLock3);
		testHelper.logTaskLockState(name);

		// Wait beyond max lock time
		log.info("Sleep for {} seconds", timeout + 1);
		await().pollDelay(Duration.ofSeconds(timeout + 1)).until(() -> true);

		// Fourth attempt should succeed as the auto-unlock mechanism should kick in
		log.info("Acquiring lock...");
		TaskLock taskLock4 = repo.tryLock(name);
		assertNotNull(taskLock4);
		testHelper.logTaskLockState(name);

		// Release lock
		log.info("Releasing lock...");
		repo.unlock(taskLock4);
		testHelper.logTaskLockState(name);
	}

}
