package jkml.data;

public interface ScheduledTaskRepositoryCustom {

	public boolean tryLock(String name);

	public void unlock(String name);

}
