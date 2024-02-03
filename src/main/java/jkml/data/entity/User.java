package jkml.data.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user")
public class User {

	public enum Role {

		MAKER("maker"), CHECKER("checker");

		private final String text;

		Role(String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}

		private static final Map<CharSequence, Role> map = new HashMap<>();

		static {
			for (Role r : values()) {
				map.put(r.toString(), r);
			}
		}

		public static Role parse(CharSequence text) {
			return map.get(text);
		}

	}

	@PrimaryKey("id")
	private UUID id;

	@Column("first_name")
	private String firstName;

	@Column("last_name")
	private String lastName;

	@Column("role")
	private Role role;

	public User() {
	}

	public User(UUID id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = Role.MAKER;
	}

	public User(UUID id, String firstName, String lastName, Role role) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return String.format("id=%s, firstName=%s, lastName=%s, role=%s", id, firstName, lastName, role.toString());
	}

}
