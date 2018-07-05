package jkml.data;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface ScheduledTaskRepository extends CassandraRepository<ScheduledTask, String> {
}
