package jkml.data.repository.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.InsertOptions;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.data.cassandra.repository.query.CassandraEntityInformation;
import org.springframework.data.cassandra.repository.support.SimpleCassandraRepository;
import org.springframework.util.Assert;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Selector;

public class CustomCassandraRepositoryImpl<T, ID> extends SimpleCassandraRepository<T, ID> implements CustomCassandraRepository<T, ID> {

	private static final InsertOptions IF_NOT_EXISTS = InsertOptions.builder().withIfNotExists().build();

	private final CassandraEntityInformation<T, ID> entityInformation;

	private final CassandraOperations operations;

	public CustomCassandraRepositoryImpl(CassandraEntityInformation<T, ID> metadata, CassandraOperations operations) {
		super(metadata, operations);
		this.entityInformation = metadata;
		this.operations = operations;
	}

	@Override
	public <S extends T> Optional<S> insertIfNotExists(S entity) {
		Assert.notNull(entity, "Entity must not be null");
		var result = this.operations.insert(entity, IF_NOT_EXISTS);
		return result.wasApplied() ? Optional.of(result.getEntity()) : Optional.empty();
	}

	@Override
	public List<ID> findPartitions() {
		var idClass = this.entityInformation.getIdType();
		var entityClass = this.entityInformation.getJavaType();
		var columnNames = PartitionKeyUtils.getColumnNames(idClass, entityClass);
		var selectors = new ArrayList<Selector>(columnNames.size());
		columnNames.forEach(c -> selectors.add(Selector.column(c)));
		var selectFrom = QueryBuilder.selectFrom(this.entityInformation.getTableName()).distinct().selectors(selectors);
		return this.operations.select(selectFrom.build(), idClass);
	}

	@Override
	public List<T> findByPartition(ID id) {
		var idClass = this.entityInformation.getIdType();
		var entityClass = this.entityInformation.getJavaType();
		var criteriaDefinitions = PartitionKeyUtils.getCriteriaDefinitions(id, idClass, entityClass);
		return this.operations.select(Query.query(criteriaDefinitions), entityClass);
	}

	@Override
	public void deleteByPartition(ID id) {
		var idClass = this.entityInformation.getIdType();
		var entityClass = this.entityInformation.getJavaType();
		var criteriaDefinitions = PartitionKeyUtils.getCriteriaDefinitions(id, idClass, entityClass);
		this.operations.delete(Query.query(criteriaDefinitions), entityClass);
	}

}
