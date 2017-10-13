package jkml;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.TypedIdCassandraRepository;

public interface UserRepository extends TypedIdCassandraRepository<User, UUID>, UserRepositoryCustom {

	@Query("SELECT * FROM user WHERE first_name=?0")
	public List<User> findByFirstName(String firstName);

	@Query("SELECT * FROM user WHERE last_name=?0")
	public List<User> findByLastName(String lastName);

}
