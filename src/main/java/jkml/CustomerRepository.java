package jkml;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface CustomerRepository extends CassandraRepository<Customer> {

	@Query("select * from customer where first_name=?0")
	public Customer findByFirstName(String firstName);

	@Query("select * from customer where last_name=?0")
	public List<Customer> findByLastName(String lastName);

}
