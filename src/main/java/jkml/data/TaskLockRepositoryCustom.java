package jkml.data;

public interface TaskLockRepositoryCustom {

	public boolean tryLock(String name);

	public void unlock(String name);

}
