package jkml.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ScanServicePackageOnlyTestApp.class)
@EnableAutoConfiguration(exclude = CassandraAutoConfiguration.class)
class DummyServiceTests {

	@Autowired
	private DummyService svc;

	@Test
	void test() {
		svc.doWork();
		assertTrue(svc.isWorkDone());
	}

}
