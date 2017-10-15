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
public class ScheduledTaskRepositoryTest {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTaskRepositoryTest.class);

	@Autowired
	private ScheduledTaskRepository repo;

	private void logTask(String name) {
		ScheduledTask st = repo.findOne(name);
		log.info("Name: {}; Locked: {}; LockTime: {}; MaxLockDuration: {}", st.getName(), st.getLocked(), st.getLockTime(), st.getMaxLockDuration());
	}

	@Test
	public void test() throws Exception {

		String taskName = "MyTask";
		int maxLockDuration = 5;

		ScheduledTask st = new ScheduledTask();
		st.setName(taskName);
		st.setLocked(false);
		st.setLockTime(null);
		st.setMaxLockDuration(maxLockDuration);
		repo.save(st);

		// First lock attempt should succeed
		assertEquals(true, repo.tryLock(taskName));
		logTask(taskName);

		// Second attempt should fail since the lock has not been released
		assertEquals(false, repo.tryLock(taskName));
		logTask(taskName);

		// Release lock
		repo.unlock(taskName);
		logTask(taskName);

		// Third attempt should succeed
		assertEquals(true, repo.tryLock(taskName));
		logTask(taskName);

		// Wait beyond max lock time
		Thread.sleep(maxLockDuration * 1000);

		// Fourth attempt should succeed as the auto-unlock mechanism should kick in
		assertEquals(true, repo.tryLock(taskName));
		logTask(taskName);
	}

}
