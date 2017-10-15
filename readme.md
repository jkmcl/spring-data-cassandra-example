A sample application demonstrating the use of the following:

* Spring Boot, Spring Data Cassandra and CassandraUnit
* Custom query methods making use of secondary indexes
* A custom Repository method that calls the high-performance ingest method of CassandraTemplate to bulk insert records
* A simple task lock implemented using Cassandra's lightweight transaction to ensure that only one one thread is executing a task at any time
