package jkml.data.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("geographic_user")
public class GeographicUser {

	@PrimaryKeyClass
	public static class GeographicUserKey {

		@PrimaryKeyColumn(name = "country", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
		private String country;

		@PrimaryKeyColumn(name = "city", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
		private String city;

		@PrimaryKeyColumn(name = "user_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
		private UUID userId;

		@Override
		public int hashCode() {
			return Objects.hash(city, country, userId);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof GeographicUserKey)) {
				return false;
			}
			GeographicUserKey other = (GeographicUserKey) obj;
			return Objects.equals(city, other.city) && Objects.equals(country, other.country)
					&& Objects.equals(userId, other.userId);
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public UUID getUserId() {
			return userId;
		}

		public void setUserId(UUID userId) {
			this.userId = userId;
		}

	}

	@PrimaryKey
	private GeographicUserKey key = new GeographicUserKey();

	@Column("create_time")
	private Instant createTime;

	@Column("update_time")
	private Instant updateTime;

	public GeographicUserKey getKey() {
		return key;
	}

	public void setKey(GeographicUserKey key) {
		this.key = key;
	}

	public Instant getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Instant createTime) {
		this.createTime = createTime;
	}

	public Instant getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Instant updateTime) {
		this.updateTime = updateTime;
	}

}
