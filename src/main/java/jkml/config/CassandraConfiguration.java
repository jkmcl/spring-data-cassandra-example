package jkml.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.core.AsyncCassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import jkml.data.repository.PackageMarker;
import jkml.data.repository.support.CustomCassandraRepositoryImpl;

@Configuration
@EnableCassandraRepositories(repositoryBaseClass = CustomCassandraRepositoryImpl.class, basePackageClasses = PackageMarker.class)
public class CassandraConfiguration {

	@Bean
	public AsyncCassandraTemplate asyncCassandraTemplate(SessionFactory sessionFactory, CassandraConverter converter) {
		return new AsyncCassandraTemplate(sessionFactory, converter);
	}

}
