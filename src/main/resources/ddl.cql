CREATE TABLE IF NOT EXISTS customer (
	id UUID, 
	first_name TEXT, 
	last_name TEXT,
	PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS customer_index0 ON customer (first_name);

CREATE INDEX IF NOT EXISTS customer_index1 ON customer (last_name);
