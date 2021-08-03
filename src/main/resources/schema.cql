-- =============================================================================
-- Users
-- =============================================================================
CREATE TABLE IF NOT EXISTS user (
	id UUID,
	first_name TEXT,
	last_name TEXT,

	PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS user_index0 ON user (first_name);

CREATE INDEX IF NOT EXISTS user_index1 ON user (last_name);

-- =============================================================================
-- Task locks
-- =============================================================================
CREATE TABLE IF NOT EXISTS task_lock (
	name TEXT,
	timeout INT,
	owner UUID,
	acquire_ts TIMESTAMP,

	PRIMARY KEY (name)
);

-- =============================================================================
-- Scheduled tasks
-- =============================================================================
CREATE TABLE IF NOT EXISTS task_schedule (
	name TEXT,
	cron_expression TEXT,
	max_ts_offset INT,
	last_start_ts TIMESTAMP,
	last_end_ts TIMESTAMP,

	PRIMARY KEY (name)
);
