CREATE TABLE IF NOT EXISTS user (
	id UUID, 
	first_name TEXT, 
	last_name TEXT,
	PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS user_index0 ON user (first_name);

CREATE INDEX IF NOT EXISTS user_index1 ON user (last_name);
