package jkml.data;

import org.springframework.data.cassandra.repository.TypedIdCassandraRepository;

public interface TaskLockRepository extends TypedIdCassandraRepository<TaskLock, String>, TaskLockRepositoryCustom {
}
