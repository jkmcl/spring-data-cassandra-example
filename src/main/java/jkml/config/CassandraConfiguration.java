package jkml.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.AsyncCassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;

import com.datastax.driver.core.Session;

@Configuration
public class CassandraConfiguration {

	/**
	 * Based on
	 * https://github.com/spring-projects/spring-boot/blob/2.2.x/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/data/cassandra/CassandraDataAutoConfiguration.java
	 */
	@Bean
	public AsyncCassandraTemplate asyncCassandraTemplate(Session session, CassandraConverter converter) {
		return new AsyncCassandraTemplate(session, converter);
	}

}
