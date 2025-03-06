package jkml.data.repository.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.EntityWriteResult;
import org.springframework.data.cassandra.core.InsertOptions;

import com.datastax.oss.driver.api.core.servererrors.CASWriteUnknownException;
import com.datastax.oss.driver.api.core.servererrors.WriteTimeoutException;

class CASUtilsTests {

	private static final Object entity = new Object();

	private static final InsertOptions options = InsertOptions.empty();

	@SuppressWarnings("unchecked")
	private static final EntityWriteResult<Object> result = mock(EntityWriteResult.class);

	@Test
	void testInsert() {
		var mockOperations = mock(CassandraOperations.class);
		when(mockOperations.insert(any(), isA(InsertOptions.class))).thenThrow(CASWriteUnknownException.class)
				.thenReturn(result);

		assertFalse(CASUtils.insert(mockOperations, entity, options).wasApplied());
	}

	@Test
	void testInsert_unhandledException() {
		var mockOperations = mock(CassandraOperations.class);
		when(mockOperations.insert(any(), isA(InsertOptions.class))).thenThrow(WriteTimeoutException.class);

		assertThrows(WriteTimeoutException.class, () -> CASUtils.insert(mockOperations, entity, options));
	}

}
