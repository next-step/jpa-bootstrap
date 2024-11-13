package persistence.sql.dml;

import persistence.meta.EntityTable;

public class DeleteQuery {
    private static class DeleteQueryHolder {
        private static final DeleteQuery INSTANCE = new DeleteQuery();
    }

    public static DeleteQuery getInstance() {
        return DeleteQueryHolder.INSTANCE;
    }

    private DeleteQuery() {
    }

    public String delete(EntityTable entityTable, Object entity) {
        return new DeleteQueryBuilder()
                .deleteFrom(entityTable.getTableName())
                .where(entityTable.getIdColumnName(), entityTable.getIdValue(entity))
                .build();
    }
}
