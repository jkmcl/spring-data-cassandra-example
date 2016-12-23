package jkml;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.RowIterator;
import org.springframework.data.cassandra.core.CassandraTemplate;

public class UserRepositoryImpl implements UserRepositoryCustom {

	private class UserRowIterator implements RowIterator {

		private final Iterator<User> iterator;

		public UserRowIterator(List<User> users) {
			this.iterator = users.iterator();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Object[] next() {
			User c = iterator.next();
			return new Object[] { c.getId(), c.getFirstName(), c.getLastName() };
		}

	}

	@Autowired
	private CassandraTemplate template;

	@Override
	public void ingest(List<User> users) {
		String cql = "INSERT INTO user (id, first_name, last_name) VALUES (?, ?, ?)";
		template.ingest(cql, new UserRowIterator(users));
	}

}
