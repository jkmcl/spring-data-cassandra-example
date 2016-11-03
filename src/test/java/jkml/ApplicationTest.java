package jkml;

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
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.base.Stopwatch;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners(mergeMode = MergeMode.MERGE_WITH_DEFAULTS, listeners = CassandraUnitDependencyInjectionIntegrationTestExecutionListener.class)
@CassandraDataSet(keyspace = "mykeyspace", value = { "ddl.cql" })
@EmbeddedCassandra
public class ApplicationTest {

	private static final Logger log = LoggerFactory.getLogger(ApplicationTest.class);

	@Autowired
	private CassandraTemplate cassTpl;

	@Autowired
	private CustomerRepository repository;

	@Autowired
	private CustomerDAO customerDAO;

	@Test
	public void testRepository() throws Exception {
		log.info("Testing CustomerRepository...");
		
		repository.deleteAll();

		// save a couple of customers
		repository.save(new Customer(UUIDs.random(), "Alice", "Smith"));
		repository.save(new Customer(UUIDs.random(), "Bob", "Smith"));

		// fetch all customers
		log.info("Customers found with findAll():");
		repository.findAll().forEach(c -> {
			log.info(c.toString());
		});

		// fetch an individual customer
		log.info("Customer found with findByFirstName('Alice'):");
		log.info(repository.findByFirstName("Alice").toString());

		log.info("Customers found with findByLastName('Smith'):");
		repository.findByLastName("Smith").forEach(c -> {
			log.info(c.toString());
		});
	}

	private List<Customer> generateRandomCustomers(int count) {
		List<Customer> list = new ArrayList<>(count);
		for (int i = 0; i < count; ++i) {
			list.add(new Customer(UUID.randomUUID(), RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10)));
		}
		return list;
	}

	@Test
	public void testTemplate() throws Exception {
		log.info("Testing CassandraTemplate...");
		
		cassTpl.selectAll(Customer.class).forEach(cus -> {
			log.info(cus.toString());
		});

		Stopwatch sw = Stopwatch.createUnstarted();

		// Generate customers
		sw.reset().start();
		List<Customer> customers = generateRandomCustomers(80000);
		sw.stop();
		log.info("Time for generating data: " + sw.elapsed(TimeUnit.MILLISECONDS) + " ms");
		log.info("Data size (record count): " + customers.size());

		// Warm up
		cassTpl.insert(generateRandomCustomers(10));
		cassTpl.deleteAll(Customer.class);

		// Insert
		sw.reset().start();
		customerDAO.insert(customers);
		sw.stop();
		log.info("Time for inserting using ingest and row iterator: " + sw.elapsed(TimeUnit.MILLISECONDS) + " ms");
	}

}
