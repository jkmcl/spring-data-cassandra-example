package jkml.data.repository;

import jkml.data.entity.TaskLock;
import jkml.data.repository.support.CustomCassandraRepository;

public interface TaskLockRepository extends CustomCassandraRepository<TaskLock, String>, TaskLockRepositoryCustom {
}
