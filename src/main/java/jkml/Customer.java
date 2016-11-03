package jkml;

import java.util.UUID;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table(value = Customer.customer)
public class Customer {
	
	public static final String customer = "customer";
	public static final String ID = "id";
	public static final String first_name = "first_name";
	public static final String last_name = "last_name";

	@PrimaryKey(value = ID)
	private UUID id;

	@Column(value = first_name)
	private String firstName;

	@Column(value = last_name)
	private String lastName;

	public Customer() {
	}

	public Customer(UUID id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return String.format("Customer[id=%s, firstName='%s', lastName='%s']", this.id,
				this.firstName, this.lastName);
	}

}
