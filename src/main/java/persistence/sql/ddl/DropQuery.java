package persistence.sql.ddl;

import persistence.meta.EntityTable;

public class DropQuery {
    private static final String QUERY_TEMPLATE = "DROP TABLE IF EXISTS %s";

    public String drop(EntityTable entityTable) {
        return QUERY_TEMPLATE.formatted(entityTable.getTableName());
    }
}
