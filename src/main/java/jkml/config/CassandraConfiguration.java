package jkml.config;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.core.AsyncCassandraOperations;
import org.springframework.data.cassandra.core.AsyncCassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import jkml.data.core.CustomCassandraOperations;
import jkml.data.entity.User.Role;
import jkml.data.repository.PackageMarker;
import jkml.data.repository.support.CustomCassandraRepositoryImpl;

@Configuration
@EnableCassandraRepositories(repositoryBaseClass = CustomCassandraRepositoryImpl.class, basePackageClasses = PackageMarker.class)
class CassandraConfiguration {

	/**
	 * Based on
	 * https://github.com/spring-projects/spring-boot/blob/2.7.x/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/data/cassandra/CassandraDataAutoConfiguration.java
	 */
	@Bean
	@ConditionalOnMissingBean
	AsyncCassandraTemplate asyncCassandraTemplate(SessionFactory sessionFactory, CassandraConverter converter) {
		return new AsyncCassandraTemplate(sessionFactory, converter);
	}

	@Bean
	CustomCassandraOperations customCassandraOperations(AsyncCassandraOperations asyncCassandraOperations) {
		return new CustomCassandraOperations(asyncCassandraOperations);
	}

	@ReadingConverter
	class RoleReadingConverter implements Converter<String, Role> {
		@Override
		public Role convert(String source) {
			return Role.parse(source);
		}
	}

	@WritingConverter
	class RoleWritingConverter implements Converter<Role, String> {
		@Override
		public String convert(Role source) {
			return source.toString();
		}
	}

	@Bean
	CassandraCustomConversions cassandraCustomConversions() {
		return new CassandraCustomConversions(Arrays.asList(new RoleReadingConverter(), new RoleWritingConverter()));
	}

}
