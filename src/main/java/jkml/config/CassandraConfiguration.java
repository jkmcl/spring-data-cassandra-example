package jkml.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.core.AsyncCassandraOperations;
import org.springframework.data.cassandra.core.AsyncCassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import jkml.data.core.CustomCassandraOperations;
import jkml.data.repository.PackageMarker;
import jkml.data.repository.support.CustomCassandraRepositoryImpl;

@Configuration
@EnableCassandraRepositories(repositoryBaseClass = CustomCassandraRepositoryImpl.class, basePackageClasses = PackageMarker.class)
public class CassandraConfiguration {

	/**
	 * Based on
	 * https://github.com/spring-projects/spring-boot/blob/2.7.x/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/data/cassandra/CassandraDataAutoConfiguration.java
	 */
	@Bean
	@ConditionalOnMissingBean
	public AsyncCassandraTemplate asyncCassandraTemplate(SessionFactory sessionFactory, CassandraConverter converter) {
		return new AsyncCassandraTemplate(sessionFactory, converter);
	}

	@Bean
	public CustomCassandraOperations customCassandraOperations(AsyncCassandraOperations asyncCassandraOperations) {
		return new CustomCassandraOperations(asyncCassandraOperations);
	}

}
