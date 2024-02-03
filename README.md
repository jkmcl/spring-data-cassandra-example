# Overview

A sample application demonstrating the use of the following:

* Spring Boot, Spring Data for Apache Cassandra and CassandraUnit
* POM tweaks to make CassandraUnit work with Cassandra 4.0.x and the latter with Spring Boot 3.2.x and Java 17
* Custom query methods utilizing secondary indexes
* A custom repository method that performs bulk insert asynchronously with number of in-flight inserts limited using a semaphore
* A custom repository base class that provides a saveIfNotExists method
* A task lock implemented using Cassandra's lightweight transaction to ensure that a task is only executed by one thread at any given time
* Two Runnable wrapper classes for synchronizing execution of scheduled tasks across multiple threads/processes
* Custom converters for converting between enums and their text values stored in Cassandra
* A helper class for selecting rows and then executing a CQL statement with bind variables for each selected row using values extracted from the row


# Eliminate Errors in Eclipse

Add the same JPMS settings in https://github.com/apache/cassandra/blob/cassandra-4.0.10/conf/jvm11-clients.options to the `.classpath` file of the Eclipse project.

```
<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-11">
	<attributes>
		<!-- Start of block for Cassandra -->
		<attribute name="module" value="true"/>
		<attribute name="add-exports" value="java.base/jdk.internal.misc=ALL-UNNAMED:java.base/jdk.internal.ref=ALL-UNNAMED:java.base/sun.nio.ch=ALL-UNNAMED:java.management.rmi/com.sun.jmx.remote.internal.rmi=ALL-UNNAMED:java.rmi/sun.rmi.registry=ALL-UNNAMED:java.rmi/sun.rmi.server=ALL-UNNAMED:java.sql/java.sql=ALL-UNNAMED"/>
		<attribute name="add-opens" value="java.base/java.lang.module=ALL-UNNAMED:java.base/jdk.internal.loader=ALL-UNNAMED:java.base/jdk.internal.ref=ALL-UNNAMED:java.base/jdk.internal.reflect=ALL-UNNAMED:java.base/jdk.internal.math=ALL-UNNAMED:java.base/jdk.internal.module=ALL-UNNAMED:java.base/jdk.internal.util.jar=ALL-UNNAMED:jdk.management/com.sun.management.internal=ALL-UNNAMED:java.base/java.io=ALL-UNNAMED:java.base/java.nio=ALL-UNNAMED:java.base/java.util=ALL-UNNAMED:java.base/java.util.concurrent=ALL-UNNAMED:java.base/java.util.concurrent.atomic=ALL-UNNAMED:java.base/sun.nio.ch=ALL-UNNAMED"/>
		<!-- End of block for Cassandra -->
		<attribute name="maven.pomderived" value="true"/>
	</attributes>
</classpathentry>

```