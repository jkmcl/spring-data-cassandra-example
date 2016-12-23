package jkml;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface UserRepository extends CassandraRepository<User>, UserRepositoryCustom {

	@Query("SELECT * FROM user WHERE first_name=?0")
	public List<User> findByFirstName(String firstName);

	@Query("SELECT * FROM user WHERE last_name=?0")
	public List<User> findByLastName(String lastName);

}
