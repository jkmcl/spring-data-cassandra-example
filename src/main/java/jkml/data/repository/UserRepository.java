package jkml.data.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.Query;

import jkml.data.entity.User;
import jkml.data.repository.support.CustomCassandraRepository;

public interface UserRepository extends CustomCassandraRepository<User, UUID>, UserRepositoryCustom {

	@Query("SELECT * FROM user WHERE first_name=?0")
	List<User> findByFirstName(String firstName);

	@Query("SELECT * FROM user WHERE last_name=?0")
	List<User> findByLastName(String lastName);

}
