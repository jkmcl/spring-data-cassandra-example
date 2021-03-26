package jkml.data.repository;

import jkml.data.entity.TaskLock;

public interface TaskLockRepositoryCustom {

	TaskLock tryLock(String name);

	void unlock(TaskLock lock);

}
