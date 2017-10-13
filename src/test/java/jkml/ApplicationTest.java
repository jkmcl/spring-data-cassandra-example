package jkml;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
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

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners(mergeMode=MergeMode.MERGE_WITH_DEFAULTS, listeners=CassandraUnitDependencyInjectionIntegrationTestExecutionListener.class)
@CassandraDataSet(keyspace="mykeyspace", value={ "ddl.cql" })
@EmbeddedCassandra
public class ApplicationTest {

	private static final Logger log = LoggerFactory.getLogger(ApplicationTest.class);

	@Autowired
	private UserRepository userRepo;

	@Test
	public void testRepository() throws Exception {
		log.info("Testing repository methods...");

		// Delete all users
		userRepo.deleteAll();

		// Save a couple of users
		userRepo.save(new User(UUID.randomUUID(), "Alice", "Smith"));
		userRepo.save(new User(UUID.randomUUID(), "Bob", "Smith"));

		// Fetch all users
		assertEquals(2, Iterables.size(userRepo.findAll()));

		// Fetch users by first name
		assertEquals(1, userRepo.findByFirstName("Alice").size());

		// Fetch users by last name
		assertEquals(2, userRepo.findByLastName("Smith").size());
	}

	private static List<User> createRandomUsers(int count) {
		List<User> list = new ArrayList<>(count);
		for (int i = 0; i < count; ++i) {
			list.add(new User(UUID.randomUUID(), RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10)));
		}
		return list;
	}

	@Test
	public void testTemplate() throws Exception {
		log.info("Testing custom repository functionality...");

		// Warm up
		userRepo.ingest(createRandomUsers(1));
		userRepo.deleteAll();

		// Create users
		int userCount = 100;
		List<User> users = createRandomUsers(userCount);

		// Insert
		Stopwatch sw = Stopwatch.createStarted();
		userRepo.ingest(users);
		sw.stop();
		log.info("Time for inserting using ingest and row iterator: " + sw.elapsed(TimeUnit.MILLISECONDS) + " ms");

		// Wait for a short while for the records to be completely inserted in the background
		Thread.sleep(3000);

		assertEquals(userCount, Iterables.size(userRepo.findAll()));
	}

}
