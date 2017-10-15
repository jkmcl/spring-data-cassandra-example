package jkml.data;

import java.util.Date;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table("scheduled_task")
public class ScheduledTask {

	@PrimaryKey("name")
	private String name;

	@Column("locked")
	private Boolean locked;

	@Column("lock_time")
	private Date lockTime;

	@Column("max_lock_duration")
	private Integer maxLockDuration;

	@Column("cron_expression")
	private String cronExpression;

	@Column("start_time")
	private Date startTime;

	@Column("max_drift_duration")
	private Integer maxDriftDuration;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Date getLockTime() {
		return lockTime;
	}

	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}

	public Integer getMaxLockDuration() {
		return maxLockDuration;
	}

	public void setMaxLockDuration(Integer maxLockDuration) {
		this.maxLockDuration = maxLockDuration;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Integer getMaxDriftDuration() {
		return maxDriftDuration;
	}

	public void setMaxDriftDuration(Integer maxDriftDuration) {
		this.maxDriftDuration = maxDriftDuration;
	}

}
