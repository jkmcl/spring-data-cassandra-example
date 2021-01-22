package jkml.data.repository;

import jkml.data.entity.ScheduledTask;
import jkml.data.repository.support.CustomCassandraRepository;

public interface ScheduledTaskRepository extends CustomCassandraRepository<ScheduledTask, String> {
}
