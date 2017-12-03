package jkml.data;

public interface TaskLockRepositoryCustom {

	public TaskLock tryLock(String name);

	public void unlock(TaskLock lock);

}
