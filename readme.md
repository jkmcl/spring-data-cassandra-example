A sample application demonstrating the use of the following:

* Spring Boot, Spring Data Cassandra and CassandraUnit
* A custom Repository method that calls the high-performance ingest method of CassandraTemplate to bulk insert records
* A TestExecutionListener implementation that starts the embedded Cassandra instance only once - all the implementations provided CassandraUnit restarts it after each test methods
