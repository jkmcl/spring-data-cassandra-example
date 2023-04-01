package jkml.data.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;

@SpringBootTest
@TestExecutionListeners(mergeMode = MergeMode.MERGE_WITH_DEFAULTS, listeners = CassandraUnitDependencyInjectionIntegrationTestExecutionListener.class)
@CassandraDataSet(keyspace = "keyspace1", value = { "schema.cql" })
@EmbeddedCassandra
class RowsProcessorTests {

	private final Logger log = LoggerFactory.getLogger(RowsProcessorTests.class);

	@Autowired
	private CqlSession session;

	private long selectCount(String tableName) {
		long count = 0;
		for (@SuppressWarnings("unused") Row row : session.execute("SELECT * FROM " + tableName)) {
			++count;
		}
		return count;
	}

	private void truncate(String tableName) {
		session.execute("TRUNCATE " + tableName);
	}

	@Test
	void test() {
		RowsProcessor processor = new RowsProcessor(session);

		log.info("Empty both tables");
		truncate("view_stats_v1");
		truncate("view_stats_v2");
		assertEquals(0, selectCount("view_stats_v1"));
		assertEquals(0, selectCount("view_stats_v2"));

		log.info("Insert rows to table v1");
		session.execute("INSERT INTO view_stats_v1 (month, views) VALUES ('Jan', 2)");
		session.execute("INSERT INTO view_stats_v1 (month, views) VALUES ('Mar', 4)");
		assertEquals(2, selectCount("view_stats_v1"));
		assertEquals(0, selectCount("view_stats_v2"));

		log.info("Test copying rows from table v1 to table v2");
		processor.process("SELECT * FROM view_stats_v1",
				"INSERT INTO view_stats_v2 (month, day_of_week, views) VALUES (?, ?, ?)",
				row -> new Object[] { row.getString("month"), "   ", row.getInt("views") });
		assertEquals(2, selectCount("view_stats_v1"));
		assertEquals(2, selectCount("view_stats_v2"));

		log.info("Empty table v1");
		truncate("view_stats_v1");
		assertEquals(0, selectCount("view_stats_v1"));
		assertEquals(2, selectCount("view_stats_v2"));

		log.info("Test copying rows from table v2 to table v1");
		processor.process("SELECT * FROM view_stats_v2", "INSERT INTO view_stats_v1 (month, views) VALUES (?, ?)",
				row -> new Object[] { row.getString("month"), row.getInt("views") });
		assertEquals(2, selectCount("view_stats_v1"));
		assertEquals(2, selectCount("view_stats_v2"));
	}

}
