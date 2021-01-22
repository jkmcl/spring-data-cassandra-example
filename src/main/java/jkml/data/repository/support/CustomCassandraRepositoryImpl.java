package jkml.data.repository.support;

import java.util.Optional;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.EntityWriteResult;
import org.springframework.data.cassandra.core.InsertOptions;
import org.springframework.data.cassandra.repository.query.CassandraEntityInformation;
import org.springframework.data.cassandra.repository.support.SimpleCassandraRepository;
import org.springframework.util.Assert;

public class CustomCassandraRepositoryImpl<T, ID> extends SimpleCassandraRepository<T, ID>
		implements CustomCassandraRepository<T, ID> {

	private static final InsertOptions IF_NOT_EXISTS = InsertOptions.builder().withIfNotExists().build();

	private final CassandraOperations operations;

	public CustomCassandraRepositoryImpl(CassandraEntityInformation<T, ID> metadata, CassandraOperations operations) {
		super(metadata, operations);
		this.operations = operations;
	}

	@Override
	public <S extends T> Optional<S> insertIfNotExists(S entity) {
		Assert.notNull(entity, "Entity must not be null");
		EntityWriteResult<S> result = operations.insert(entity, IF_NOT_EXISTS);
		return result.wasApplied() ? Optional.of(result.getEntity()) : Optional.empty();
	}

}
