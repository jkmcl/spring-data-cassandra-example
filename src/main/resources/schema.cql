-- =============================================================================
-- Users
-- =============================================================================
CREATE TABLE IF NOT EXISTS user (
	id UUID,
	first_name TEXT,
	last_name TEXT,
	role TEXT,

	PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS user_index0 ON user (first_name);

CREATE INDEX IF NOT EXISTS user_index1 ON user (last_name);

-- =============================================================================
-- Geographic users
-- =============================================================================
CREATE TABLE IF NOT EXISTS geographic_user (
	country     TEXT,
	city        TEXT,
	user_id     UUID,
	create_time TIMESTAMP,
	update_time TIMESTAMP,

	PRIMARY KEY ((country, city), user_id)
);

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

-- =============================================================================
-- Data migration tables
-- =============================================================================
CREATE TABLE IF NOT EXISTS view_stats_v1 (
	month TEXT,
	views INT,

	PRIMARY KEY (month)
);

CREATE TABLE IF NOT EXISTS view_stats_v2 (
	month TEXT,
	day_of_week TEXT,
	views INT,

	PRIMARY KEY (month, day_of_week)
);
