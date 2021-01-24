package jkml.data.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;

import jkml.data.entity.ScheduledTask;

public interface ScheduledTaskRepository extends CassandraRepository<ScheduledTask, String> {
}
