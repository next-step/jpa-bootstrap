package persistence.entity;

import persistence.sql.definition.ColumnDefinitionAware;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EntitySnapshot {
    private final Map<String, Object> columnSnapshots = new HashMap<>();

    public EntitySnapshot(Object entity, EntityPersister entityPersister) {
        final List<? extends ColumnDefinitionAware> columns = entityPersister.getColumns();

        for (ColumnDefinitionAware column : columns) {
            final Object value = getNullableValue(column, entityPersister, entity);
            columnSnapshots.put(column.getDatabaseColumnName(), value);
        }
    }

    private static Object getNullableValue(ColumnDefinitionAware column, EntityPersister entityPersister, Object entity) {
        return entityPersister.hasValue(entity, column) ? quoted(entityPersister.getValue(entity, column)) : null;
    }

    private static String quoted(Object value) {
        return value instanceof String ? "'" + value + "'" : value.toString();
    }

    public boolean hasDirtyColumns(Object managedEntity, EntityPersister entityPersister) {
        final List<? extends ColumnDefinitionAware> columns = entityPersister.getColumns();
        return columns.stream()
                .anyMatch(column -> {
                            final Object entityValue = getNullableValue(column, entityPersister, managedEntity);
                            final Object snapshotValue = this.columnSnapshots.get(column.getDatabaseColumnName());
                            return !Objects.equals(entityValue, snapshotValue);
                        }
                );
    }
}
