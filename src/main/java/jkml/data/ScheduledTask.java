package jkml.data;

import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("scheduled_task")
public class ScheduledTask {

	@PrimaryKey("name")
	private String name;

	@Column("cron_expression")
	private String cronExpression;

	@Column("max_ts_offset")
	private int maxTsOffset;

	@Column("last_start_ts")
	private Date lastStartTs;

	@Column("last_end_ts")
	private Date lastEndTs;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public int getMaxTsOffset() {
		return maxTsOffset;
	}

	public void setMaxTsOffset(int maxTsOffset) {
		this.maxTsOffset = maxTsOffset;
	}

	public Date getLastStartTs() {
		return lastStartTs;
	}

	public void setLastStartTs(Date lastStartTs) {
		this.lastStartTs = lastStartTs;
	}

	public Date getLastEndTs() {
		return lastEndTs;
	}

	public void setLastEndTs(Date lastEndTs) {
		this.lastEndTs = lastEndTs;
	}

}
