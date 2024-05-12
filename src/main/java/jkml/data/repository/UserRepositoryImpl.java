package jkml.data.repository;

import java.util.List;

import jkml.data.core.CustomCassandraOperations;
import jkml.data.entity.User;

public class UserRepositoryImpl implements UserRepositoryCustom {

	private final CustomCassandraOperations customOperations;

	public UserRepositoryImpl(CustomCassandraOperations customOperations) {
		this.customOperations = customOperations;
	}

	@Override
	public void ingest(List<User> users) {
		customOperations.ingest(users);
	}

}
