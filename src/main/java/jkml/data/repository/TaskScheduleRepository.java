package jkml.data.repository;

import jkml.data.entity.TaskSchedule;
import jkml.data.repository.support.CustomCassandraRepository;

public interface TaskScheduleRepository extends CustomCassandraRepository<TaskSchedule, String> {
}
