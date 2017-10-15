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

import jkml.data.ScheduledTask;
import jkml.data.ScheduledTaskRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners(mergeMode=MergeMode.MERGE_WITH_DEFAULTS, listeners=CassandraUnitDependencyInjectionIntegrationTestExecutionListener.class)
@CassandraDataSet(keyspace="mykeyspace", value={ "ddl.cql" })
@EmbeddedCassandra
public class DistributedRunnableTest {

	private static final Logger log = LoggerFactory.getLogger(DistributedRunnableTest.class);

	@Autowired
	private ScheduledTaskRepository taskRepo;

	@Test
	public void test() throws Exception {
		log.info("Setting up a task in DB...");
		ScheduledTask st = new ScheduledTask();
		st.setName("MyTask");
		st.setLocked(false);
		st.setLockTime(null);
		st.setMaxLockDuration(10);
		taskRepo.save(st);

		Runnable task = new Runnable() {
			@Override
			public void run() {
				System.out.println("Hello world!");
			}
		};

		log.info("Run plain task");
		task.run();

		DistributedRunnable dr = new DistributedRunnable(taskRepo, "MyTask", task);

		log.info("Run wrapped task");
		dr.run();
	}

}
