package jkml.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import jkml.data.repository.PackageMarker;
import jkml.data.repository.support.CustomCassandraRepositoryImpl;

@Configuration
@EnableCassandraRepositories(repositoryBaseClass = CustomCassandraRepositoryImpl.class, basePackageClasses = PackageMarker.class)
public class CassandraConfiguration {
}
