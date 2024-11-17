package persistence.sql.dml;

import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateQuery {
    private static class UpdateQueryHolder {
        private static final UpdateQuery INSTANCE = new UpdateQuery();
    }

    public static UpdateQuery getInstance() {
        return UpdateQueryHolder.INSTANCE;
    }

    private UpdateQuery() {
    }

    public String update(EntityTable entityTable, List<EntityColumn> entityColumns, Object entity) {
        return new UpdateQueryBuilder()
                .update(entityTable.getTableName())
                .set(getSetColumns(entityColumns), getSetValues(entityColumns, entity))
                .where(entityTable.getIdColumnName(), entityTable.getIdValue(entity))
                .build();
    }

    private List<String> getSetColumns(List<EntityColumn> entityColumns) {
        return entityColumns.stream()
                .map(EntityColumn::getColumnName)
                .collect(Collectors.toList());
    }

    private List<Object> getSetValues(List<EntityColumn> entityColumns, Object entity) {
        return entityColumns.stream()
                .map(entityColumn -> entityColumn.extractValue(entity))
                .collect(Collectors.toList());
    }
}
