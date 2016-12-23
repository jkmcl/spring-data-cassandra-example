package jkml;

import org.cassandraunit.spring.AbstractCassandraUnitTestExecutionListener;
import org.cassandraunit.spring.CassandraUnitTestExecutionListener;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

/**
 * This {@link TestExecutionListener} differs from {@link CassandraUnitTestExecutionListener}
 * in that the database is started and loaded only once before Spring dependency injection, not after each test
 */
public class MyCassandraUnitTestExecutionListener extends AbstractCassandraUnitTestExecutionListener {

	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {
		startServer(testContext);
	}

	@Override
	public void afterTestClass(TestContext testContext) throws Exception {
		cleanServer();
	}

}
