package jkml.data.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.UUID;

import org.cassandraunit.spring.CassandraDataSet;
import org.cassandraunit.spring.CassandraUnitDependencyInjectionIntegrationTestExecutionListener;
import org.cassandraunit.spring.EmbeddedCassandra;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;

import jkml.data.entity.GeographicUser;

@SpringBootTest
@TestExecutionListeners(mergeMode = MergeMode.MERGE_WITH_DEFAULTS, listeners = CassandraUnitDependencyInjectionIntegrationTestExecutionListener.class)
@CassandraDataSet(keyspace = "keyspace1", value = { "schema.cql" })
@EmbeddedCassandra
class GeographicUserRepositoryTests {

	private final Logger log = LoggerFactory.getLogger(GeographicUserRepositoryTests.class);

	@Autowired
	private GeographicUserRepository repo;

	@BeforeEach
	void beforeEach() {
		repo.deleteAll();
	}

	private static GeographicUser createUser(String country, String city, UUID userId) {
		var now = Instant.now();
		var entity = new GeographicUser();
		entity.getKey().setCountry(country);
		entity.getKey().setCity(city);
		entity.getKey().setUserId(userId);
		entity.setCreateTime(now);
		entity.setUpdateTime(now);
		return entity;
	}

	@Test
	void test() {
		log.info("Sanity tests...");

		repo.insert(createUser("USA", "New York", UUID.randomUUID()));
		repo.insert(createUser("UK", "London", UUID.randomUUID()));
		repo.insert(createUser("Canada", "Toronto", UUID.randomUUID()));

		assertEquals(3, repo.count());
	}

	@Test
	void testByPartition() throws Exception {
		var user = createUser("USA", "New York", UUID.randomUUID());
		var userKey = user.getKey();

		repo.insert(user);
		assertEquals(1, repo.findByPartition(userKey).size());

		var partitions = repo.findPartitions();
		assertEquals(1, partitions.size());
		var partition = partitions.get(0);
		assertEquals("USA", partition.getCountry());
		assertEquals("New York", partition.getCity());
		assertNull(partition.getUserId());

		repo.deleteByPartition(userKey);
		assertEquals(0, repo.findByPartition(userKey).size());
	}

}
