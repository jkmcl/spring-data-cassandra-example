package jkml.data;

import org.springframework.data.cassandra.repository.TypedIdCassandraRepository;

public interface ScheduledTaskRepository extends TypedIdCassandraRepository<ScheduledTask, String> {
}
