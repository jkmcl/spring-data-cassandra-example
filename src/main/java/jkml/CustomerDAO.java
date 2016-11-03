package jkml;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.RowIterator;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerDAO {

	private class CustomerRowIterator implements RowIterator {

		private final Iterator<Customer> iterator;

		public CustomerRowIterator(List<Customer> customers) {
			this.iterator = customers.iterator();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Object[] next() {
			Customer c = iterator.next();
			return new Object[] { c.getId(), c.getFirstName(), c.getLastName() };
		}

	}

	@Autowired
	private CassandraTemplate cassTpl;

	public void insert(List<Customer> customers) {
		String cql = "insert into customer (id, first_name, last_name) values (?, ?, ?)";
		cassTpl.ingest(cql, new CustomerRowIterator(customers));
	}

}
