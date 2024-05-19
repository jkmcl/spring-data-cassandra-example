package jkml.data.repository.support;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;

public class MockEntity {

	@PrimaryKey("id")
	public int id;

	public MockEntity() {
	}

	public MockEntity(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
