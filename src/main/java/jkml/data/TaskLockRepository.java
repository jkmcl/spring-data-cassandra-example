package jkml.data;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface TaskLockRepository extends CassandraRepository<TaskLock, String>, TaskLockRepositoryCustom {
}
