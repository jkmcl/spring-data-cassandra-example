package jkml.data;

import static org.junit.Assert.assertEquals;

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

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners(mergeMode=MergeMode.MERGE_WITH_DEFAULTS, listeners=CassandraUnitDependencyInjectionIntegrationTestExecutionListener.class)
@CassandraDataSet(keyspace="mykeyspace", value={ "ddl.cql" })
@EmbeddedCassandra
public class TaskLockRepositoryTest {

	private final Logger log = LoggerFactory.getLogger(TaskLockRepositoryTest.class);

	@Autowired
	private TaskLockRepository repo;

	@Autowired
	private RepoTestHelper testHelper;

	@Test
	public void test() throws Exception {

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
		assertEquals(true, repo.tryLock(name));
		testHelper.logTaskLockState(name);

		// Second attempt should fail since the lock has not been released
		log.info("Acquiring lock...");
		assertEquals(false, repo.tryLock(name));
		testHelper.logTaskLockState(name);

		// Release lock
		log.info("Releasing lock...");
		repo.unlock(name);
		testHelper.logTaskLockState(name);

		// Third attempt should succeed
		log.info("Acquiring lock...");
		assertEquals(true, repo.tryLock(name));
		testHelper.logTaskLockState(name);

		// Wait beyond max lock time
		log.info("Sleep for {} seconds", timeout + 1);
		Thread.sleep((timeout + 1) * 1000);

		// Fourth attempt should succeed as the auto-unlock mechanism should kick in
		log.info("Acquiring lock...");
		assertEquals(true, repo.tryLock(name));
		testHelper.logTaskLockState(name);
	}

}