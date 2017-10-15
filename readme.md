A sample application demonstrating the use of the following:

* Spring Boot, Spring Data Cassandra and CassandraUnit
* Custom query methods making use of secondary indexes
* A custom Repository method that calls the high-performance ingest method of CassandraTemplate to bulk insert records
* A simple task lock implemented using Cassandra's lightweight transaction to ensure that a task is only executed by one thread at any time
* Two Runnable wrapper classes for synchronizing execution of scheduled tasks across multiple threads/processes
