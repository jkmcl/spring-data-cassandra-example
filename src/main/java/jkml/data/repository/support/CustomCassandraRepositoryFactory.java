package jkml.data.repository.support;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.repository.support.CassandraRepositoryFactory;
import org.springframework.data.repository.core.RepositoryInformation;

import com.datastax.oss.driver.api.core.CqlSession;

/**
 * A custom factory for passing {@link CqlSession} to our custom repository base
 * class.
 *
 * @see https://stackoverflow.com/a/45345597
 */
class CustomCassandraRepositoryFactory extends CassandraRepositoryFactory {

	private final CqlSession session;

	public CustomCassandraRepositoryFactory(CassandraOperations operations, CqlSession session) {
		super(operations);
		this.session = session;
	}

	@Override
	protected Object getTargetRepository(RepositoryInformation information) {
		var targetRepository = super.getTargetRepository(information);

		// Provide CqlSession to the repository for creating prepare statements
		if (targetRepository instanceof CustomCassandraRepositoryImpl<?, ?> impl) {
			impl.createSelectDistinct(this.session);
		}

		return targetRepository;
	}

}
