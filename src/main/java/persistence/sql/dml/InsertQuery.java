package persistence.sql.dml;

import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;

import java.util.List;
import java.util.stream.Collectors;

public class InsertQuery {
    private static class InsertQueryHolder {
        private static final InsertQuery INSTANCE = new InsertQuery();
    }

    public static InsertQuery getInstance() {
        return InsertQueryHolder.INSTANCE;
    }

    private InsertQuery() {
    }

    public String insert(EntityTable entityTable, Object entity) {
        return new InsertQueryBuilder()
                .insertInto(entityTable.getTableName(), getColumns(entityTable))
                .values(getValues(entityTable, entity))
                .build();
    }

    public String insert(EntityTable entityTable, String columnName, Object associationId, Object entity) {
        return new InsertQueryBuilder()
                .insertInto(entityTable.getTableName(), getColumns(entityTable, columnName))
                .values(getValues(entityTable, associationId, entity))
                .build();
    }

    private List<String> getColumns(EntityTable entityTable, String columnName) {
        final List<String> columnClause = getColumns(entityTable);
        columnClause.add(columnName);
        return columnClause;
    }

    private List<String> getColumns(EntityTable entityTable) {
        return entityTable.getEntityColumns()
                .stream()
                .filter(this::isAvailable)
                .map(EntityColumn::getColumnName)
                .collect(Collectors.toList());
    }

    private List<Object> getValues(EntityTable entityTable, Object associationId, Object entity) {
        final List<Object> valueClause = getValues(entityTable, entity);
        valueClause.add(associationId);
        return valueClause;
    }

    private List<Object> getValues(EntityTable entityTable, Object entity) {
        return entityTable.getEntityColumns()
                .stream()
                .filter(this::isAvailable)
                .map(entityColumn -> entityColumn.extractValue(entity))
                .collect(Collectors.toList());
    }

    private boolean isAvailable(EntityColumn entityColumn) {
        return !entityColumn.isGenerationValue() && !entityColumn.isOneToMany();
    }
}
