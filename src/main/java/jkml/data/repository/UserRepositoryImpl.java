package jkml.data.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import jkml.data.core.CustomCassandraOperations;
import jkml.data.entity.User;

public class UserRepositoryImpl implements UserRepositoryCustom {

	@Autowired
	private CustomCassandraOperations customOperations;

	@Override
	public void ingest(List<User> users) {
		customOperations.ingest(users);
	}

}
