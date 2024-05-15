package jkml.data.repository.support;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraPrimaryKeyColumnAnnotationComparator;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.CriteriaDefinition;
import org.springframework.util.ReflectionUtils;

public class PartitionUtils {

	private PartitionUtils() {
	}

	private static boolean isPrimaryKeyClass(Class<?> idClass) {
		return AnnotationUtils.findAnnotation(idClass, PrimaryKeyClass.class) != null;
	}

	static Map<String, Field> getPartitionKeyColumns(Class<?> primaryKeyClass) {
		var fields = new TreeMap<PrimaryKeyColumn, Field>(CassandraPrimaryKeyColumnAnnotationComparator.INSTANCE);
		for (var field : primaryKeyClass.getDeclaredFields()) {
			var pk = field.getDeclaredAnnotation(PrimaryKeyColumn.class);
			if (pk != null && PrimaryKeyType.PARTITIONED.equals(pk.type())) {
				fields.put(pk, field);
			}
		}

		var result = new LinkedHashMap<String, Field>();
		for (var entry : fields.entrySet()) {
			result.put(MergedAnnotations.from(entry.getKey()).get(Column.class).getString("value"), entry.getValue());
		}
		return result;
	}

	public static String getPartitionKeyColumnName(Class<?> entityClass) {
		for (var field : entityClass.getDeclaredFields()) {
			var pk = field.getDeclaredAnnotation(PrimaryKey.class);
			if (pk != null) {
				return MergedAnnotations.from(pk).get(Column.class).getString("value");
			}
		}
		return null;
	}

	public static List<String> getPartitionKeyColumnNames(Map<String, Field> columns) {
		var result = new ArrayList<String>(columns.size());
		columns.keySet().forEach(result::add);
		return List.copyOf(result);
	}

	public static CriteriaDefinition getCriteriaDefinition(String columnName, Object id) {
		return Criteria.where(columnName).is(id);
	}

	public static List<CriteriaDefinition> getCriteriaDefinitions(Map<String, Field> columns, Object id) {
		var result = new ArrayList<CriteriaDefinition>(columns.size());
		for (var entry : columns.entrySet()) {
			var field = entry.getValue();
			field.setAccessible(true);
			result.add(Criteria.where(entry.getKey()).is(ReflectionUtils.getField(field, id)));
		}
		return List.copyOf(result);
	}

	public static List<String> getColumnNames(Class<?> idClass, Class<?> entityClass) {
		return isPrimaryKeyClass(idClass) ? getPartitionKeyColumnNames(getPartitionKeyColumns(idClass))
				: List.of(getPartitionKeyColumnName(entityClass));
	}

	public static List<CriteriaDefinition> getCriteriaDefinitions(Object id, Class<?> idClass, Class<?> entityClass) {
		return isPrimaryKeyClass(idClass) ? getCriteriaDefinitions(getPartitionKeyColumns(idClass), id)
				: List.of(getCriteriaDefinition(getPartitionKeyColumnName(entityClass), id));
	}

}
