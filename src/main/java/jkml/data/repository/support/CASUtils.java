package jkml.data.repository.support;

import org.springframework.dao.DataAccessException;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.EntityWriteResult;
import org.springframework.data.cassandra.core.InsertOptions;

import com.datastax.oss.driver.api.core.servererrors.CASWriteUnknownException;

public class CASUtils {

	private CASUtils() {
	}

	public static <T> EntityWriteResult<T> insert(CassandraOperations operations, T entity, InsertOptions options) throws DataAccessException {
		do {
			try {
				return operations.insert(entity, options);
			} catch (CASWriteUnknownException e) {
				// Retry infinitely
			}
		} while (true);
	}

}
