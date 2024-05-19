package jkml.data.repository.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PartitionKeyUtilsTests {

	@Test
	void testGetColumns() {
		var columns = PartitionKeyUtils.getColumns(MockId.class);
		assertEquals(2, columns.size());
		assertTrue(columns.containsKey("pk1"));
		assertTrue(columns.containsKey("pk2"));
	}

	@Test
	void testGetColumns_notFound() {
		assertThrows(IllegalArgumentException.class, () -> PartitionKeyUtils.getColumns(String.class));
	}

	@Test
	void testGetColumnName_notFound() {
		assertThrows(IllegalArgumentException.class, () -> PartitionKeyUtils.getColumnName(String.class));
	}

	@Test
	void testGetColumnNames() {
		var names = PartitionKeyUtils.getColumnNames(MockId.class, String.class);
		assertEquals(2, names.size());
		assertEquals("pk1", names.get(0));
		assertEquals("pk2", names.get(1));

		names = PartitionKeyUtils.getColumnNames(String.class, MockEntity.class);
		assertEquals(1, names.size());
		assertEquals("id", names.get(0));
	}

	@Test
	void testGetCriteriaDefinitions() {
		var entity = new MockEntity(0);
		var definitions = PartitionKeyUtils.getCriteriaDefinitions(entity, Integer.class, MockEntity.class);
		assertEquals(1, definitions.size());

		var id = new MockId();
		id.setPartition1(0);
		id.setPartition2(0);
		id.setClustering1(0);
		definitions = PartitionKeyUtils.getCriteriaDefinitions(id, MockId.class, String.class);
		assertEquals(2, definitions.size());
	}

}
