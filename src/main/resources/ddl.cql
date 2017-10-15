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
-- Info for ensuring that a single application instance is executing the same 
-- task at any time
-- =============================================================================
CREATE TABLE IF NOT EXISTS scheduled_task (
	name TEXT,
	locked BOOLEAN,
	lock_time TIMESTAMP,
	max_lock_duration INT,

	cron_expression TEXT,
	start_time TIMESTAMP,
	max_drift_duration INT,	

	PRIMARY KEY (name)
);
