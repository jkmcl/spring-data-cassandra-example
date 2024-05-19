package jkml.data.repository.support;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class MockId {

	@PrimaryKeyColumn(name = "pk1", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private int partition1;

	@PrimaryKeyColumn(name = "pk2", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private int partition2;

	@PrimaryKeyColumn(name = "pk2", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
	private int clustering1;

	public int getPartition1() {
		return partition1;
	}

	public void setPartition1(int partition1) {
		this.partition1 = partition1;
	}

	public int getPartition2() {
		return partition2;
	}

	public void setPartition2(int partition2) {
		this.partition2 = partition2;
	}

	public int getClustering1() {
		return clustering1;
	}

	public void setClustering1(int clustering1) {
		this.clustering1 = clustering1;
	}

}
