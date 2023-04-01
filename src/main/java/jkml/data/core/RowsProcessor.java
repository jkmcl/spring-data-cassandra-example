package jkml.data.core;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

public class RowsProcessor {

	private final Logger log = LoggerFactory.getLogger(RowsProcessor.class);

	private final CqlSession session;

	public RowsProcessor(CqlSession cqlSession) {
		session = cqlSession;
	}

	/**
	 * Fetches rows with a query and execute another query for each row using values
	 * extracted from the row.
	 *
	 * @param selectQuery   CQL statement for fetching rows
	 * @param perRowQuery   CQL statement with bind variables to be executed for
	 *                      each fetched row
	 * @param getBindValues Function that extracts values from each row to be bound
	 */
	public void process(String selectQuery, String perRowQuery, Function<Row, Object[]> getBindValues) {
		log.info("Fetching rows with statement: {}", selectQuery);
		ResultSet rs = session.execute(selectQuery);

		log.info("Binding values from each row to execute statement: {}", perRowQuery);
		PreparedStatement stmt = session.prepare(perRowQuery);
		long count = 0;
		for (Row row : rs) {
			session.execute(stmt.bind(getBindValues.apply(row)));
			++count;
		}

		log.info("Row count: {}", count);
	}

}
