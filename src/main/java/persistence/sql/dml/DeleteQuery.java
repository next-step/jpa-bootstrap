package persistence.sql.dml;

import persistence.meta.EntityTable;

public class DeleteQuery {
    public String delete(EntityTable entityTable, Object entity) {
        return new DeleteQueryBuilder()
                .deleteFrom(entityTable.getTableName())
                .where(entityTable.getIdColumnName(), entityTable.getIdValue(entity))
                .build();
    }
}
