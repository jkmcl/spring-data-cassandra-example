A sample application demonstrating the use of the following:

* Spring Boot, Spring Data Cassandra and CassandraUnit
* Custom query methods making use of secondary indexes
* A custom repository method that performs bulk insert asynchronously with number of in-flight inserts limited using a semaphore
* A custom repository base class that provides a saveIfNotExists method
* A task lock implemented using Cassandra's lightweight transaction to ensure that a task is only executed by one thread at any time
* Two Runnable wrapper classes for synchronizing execution of scheduled tasks across multiple threads/processes
* Customized org.apache.cassandra.io.sstable.Descriptor and org.apache.cassandra.io.sstable.format.SSTableFormat under src/test to make cassandra-all work with newer versions of Guava
