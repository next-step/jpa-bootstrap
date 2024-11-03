package persistence.sql.dml;

import persistence.meta.EntityTable;

public class DeleteQuery {
    public String delete(Object entity) {
        final EntityTable entityTable = new EntityTable(entity);
        final Object id = entityTable.getIdValue();

        return new DeleteQueryBuilder()
                .deleteFrom(entityTable.getTableName())
                .where(entityTable.getIdColumnName(), entityTable.getIdValue())
                .build();
    }
}
