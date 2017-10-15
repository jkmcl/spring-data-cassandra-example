package jkml.data;

import java.util.Date;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table("task_lock")
public class TaskLock {

	@PrimaryKey("name")
	private String name;

	@Column("timeout")
	private int timeout;

	@Column("acquired")
	private boolean acquired;

	@Column("acquire_ts")
	private Date acquireTs;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isAcquired() {
		return acquired;
	}

	public void setAcquired(boolean acquired) {
		this.acquired = acquired;
	}

	public Date getAcquireTs() {
		return acquireTs;
	}

	public void setAcquireTs(Date acquisitionTs) {
		this.acquireTs = acquisitionTs;
	}

}
