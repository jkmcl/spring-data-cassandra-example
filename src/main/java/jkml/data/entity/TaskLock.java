package jkml.data.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("task_lock")
public class TaskLock {

	@PrimaryKey("name")
	private String name;

	@Column("timeout")
	private int timeout;

	@Column("owner")
	private UUID owner;

	@Column("acquire_ts")
	private Instant acquireTs;

	public TaskLock() {
	}

	public TaskLock(String name) {
		this.name = name;
	}

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

	public Instant getAcquireTs() {
		return acquireTs;
	}

	public void setAcquireTs(Instant acquireTs) {
		this.acquireTs = acquireTs;
	}

}
