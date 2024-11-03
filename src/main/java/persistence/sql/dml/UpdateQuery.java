package persistence.sql.dml;

import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateQuery {
    public String update(Object entity, List<EntityColumn> entityColumns) {
        final EntityTable entityTable = new EntityTable(entity);
        return new UpdateQueryBuilder()
                .update(entityTable.getTableName())
                .set(getSetColumns(entityColumns), getSetValues(entityColumns))
                .where(entityTable.getIdColumnName(), entityTable.getIdValue())
                .build();
    }

    private List<String> getSetColumns(List<EntityColumn> entityColumns) {
        return entityColumns.stream()
                .map(EntityColumn::getColumnName)
                .collect(Collectors.toList());
    }

    private List<Object> getSetValues(List<EntityColumn> entityColumns) {
        return entityColumns.stream()
                .map(EntityColumn::getValue)
                .collect(Collectors.toList());
    }
}
