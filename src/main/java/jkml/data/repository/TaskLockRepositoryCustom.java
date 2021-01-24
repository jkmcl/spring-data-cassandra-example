package jkml.data.repository;

import jkml.data.entity.TaskLock;

public interface TaskLockRepositoryCustom {

	public TaskLock tryLock(String name);

	public void unlock(TaskLock lock);

	public boolean isLockingEnabled();

}
