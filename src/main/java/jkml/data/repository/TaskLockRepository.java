package jkml.data.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;

import jkml.data.entity.TaskLock;

public interface TaskLockRepository extends CassandraRepository<TaskLock, String>, TaskLockRepositoryCustom {
}
