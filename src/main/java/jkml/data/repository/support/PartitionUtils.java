package jkml.data.repository.support;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

	public static String getPartitionKeyColumnName(Class<?> entityClass) {
		for (var field : entityClass.getDeclaredFields()) {
			var annotation = field.getDeclaredAnnotation(PrimaryKey.class);
			if (annotation != null) {
				return MergedAnnotations.from(annotation).get(Column.class).getString("value");
			}
		}
		return null;
	}

	public static Map<String, Field> getPartitionKeyColumns(Class<?> primaryKeyClass) {
		var columns = new ArrayList<PrimaryKeyColumn>();
		var fields = new HashMap<PrimaryKeyColumn, Field>();
		for (var field : primaryKeyClass.getDeclaredFields()) {
			var annotation = field.getDeclaredAnnotation(PrimaryKeyColumn.class);
			if (annotation != null && PrimaryKeyType.PARTITIONED.equals(annotation.type())) {
				fields.put(annotation, field);
				columns.add(annotation);
			}
		}

		columns.sort(CassandraPrimaryKeyColumnAnnotationComparator.INSTANCE);

		var sortedFields = new LinkedHashMap<String, Field>();
		for (var col : columns) {
			sortedFields.put(MergedAnnotations.from(col).get(Column.class).getString("value"), fields.get(col));
		}
		return sortedFields;
	}

	public static List<CriteriaDefinition> getCriteriaDefinition(String columnName, Object id) {
		return List.of(Criteria.where(columnName).is(id));
	}

	public static List<CriteriaDefinition> getCriteriaDefinitions(Map<String, Field> columns, Object id) {
		var result = new ArrayList<CriteriaDefinition>(columns.size());
		for (var entry : columns.entrySet()) {
			var field = entry.getValue();
			field.setAccessible(true);
			result.add(Criteria.where(entry.getKey()).is(ReflectionUtils.getField(field, id)));
		}
		return result;
	}

	public static List<String> getColumnNames(Class<?> idClass, Class<?> entityClass) {
		var columnNames = new ArrayList<String>();
		if (isPrimaryKeyClass(idClass)) {
			getPartitionKeyColumns(idClass).keySet().forEach(columnNames::add);
		} else {
			columnNames.add(getPartitionKeyColumnName(entityClass));
		}
		return columnNames;
	}

	public static List<CriteriaDefinition> getCriteriaDefinitions(Object id, Class<?> idClass, Class<?> entityClass) {
		if (isPrimaryKeyClass(idClass)) {
			return getCriteriaDefinitions(getPartitionKeyColumns(idClass), id);
		} else {
			return getCriteriaDefinition(getPartitionKeyColumnName(entityClass), id);
		}
	}

}
