package jkml.data.repository.support;

import java.util.List;
import java.util.Optional;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CustomCassandraRepository<T, ID> extends CassandraRepository<T, ID> {

	<S extends T> Optional<S> insertIfNotExists(S entity);

	List<ID> findPartitions();

	List<T> findByPartition(ID id);

	void deleteByPartition(ID id);

}
