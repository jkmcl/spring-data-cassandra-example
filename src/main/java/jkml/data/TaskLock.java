package jkml.data;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table("task_lock")
public class TaskLock {

	@PrimaryKey("name")
	private String name;

	@Column("timeout")
	private int timeout;

	@Column("owner")
	private UUID owner;

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

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public Date getAcquireTs() {
		return acquireTs;
	}

	public void setAcquireTs(Date acquireTs) {
		this.acquireTs = acquireTs;
	}

}
