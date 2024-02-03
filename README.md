# Overview

A sample Spring Boot application demonstrating how to use CassandraUnit and an embedded instance of Cassandra 4.0 to run JUnit tests on Java 17:
* CassandraUnit has been inactive since 2020 and its last version only supports Cassandra 3.11
* Cassandra 4.0 does not support Java 17
* Spring Boot 3.2 uses some dependencies of Cassandra but at versions with incompatible method signatures

Features:
* POM tweaks and customized Cassandra classes to make CassandraUnit and Cassandra 4.0 work with Spring Boot 3.2 on Java 17
* Custom query methods utilizing secondary indexes
* A custom repository method that performs bulk insert asynchronously with number of in-flight inserts limited using a semaphore
* A custom repository base class that provides a saveIfNotExists method
* A task lock implemented using Cassandra's lightweight transaction to ensure that a task is only executed by one thread at any given time
* Two Runnable wrapper classes for synchronizing execution of scheduled tasks across multiple threads/processes
* Custom converters for converting between enums and their text values stored in Cassandra
* A helper class for selecting rows and then executing a CQL statement with bind variables for each selected row using values extracted from the row


# Eliminate Errors in Eclipse

Uncheck `Use '--release' option` in the project's Java Compiler settings.

Add the JPMS options in https://github.com/apache/cassandra/blob/cassandra-4.0.12/conf/jvm11-clients.options and a few additional ones (for Java 17) to the `.classpath` file of the project:

```
<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-17">
	<attributes>
		<attribute name="maven.pomderived" value="true"/>
		<!-- Start of JPMS options for Cassandra -->
		<attribute name="module" value="true"/>
		<attribute name="add-exports" value="java.base/jdk.internal.misc=ALL-UNNAMED:java.base/jdk.internal.ref=ALL-UNNAMED:java.base/sun.nio.ch=ALL-UNNAMED:java.management.rmi/com.sun.jmx.remote.internal.rmi=ALL-UNNAMED:java.rmi/sun.rmi.registry=ALL-UNNAMED:java.rmi/sun.rmi.server=ALL-UNNAMED:java.sql/java.sql=ALL-UNNAMED"/>
		<attribute name="add-opens" value="java.base/java.lang.module=ALL-UNNAMED:java.base/jdk.internal.loader=ALL-UNNAMED:java.base/jdk.internal.ref=ALL-UNNAMED:java.base/jdk.internal.reflect=ALL-UNNAMED:java.base/jdk.internal.math=ALL-UNNAMED:java.base/jdk.internal.module=ALL-UNNAMED:java.base/jdk.internal.util.jar=ALL-UNNAMED:jdk.management/com.sun.management.internal=ALL-UNNAMED:java.base/java.io=ALL-UNNAMED:java.base/java.lang=ALL-UNNAMED:java.base/java.nio=ALL-UNNAMED:java.base/java.util=ALL-UNNAMED:java.base/java.util.concurrent=ALL-UNNAMED:java.base/java.util.concurrent.atomic=ALL-UNNAMED:java.base/sun.nio.ch=ALL-UNNAMED"/>
		<!-- End of JPMS options for Cassandra -->
	</attributes>
</classpathentry>
```
