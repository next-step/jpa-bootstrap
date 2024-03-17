package persistence.sql.dml;

import persistence.sql.meta.IdColumn;
import persistence.sql.meta.Table;

public class DeleteQueryBuilder {
    private static final String DELETE_QUERY_TEMPLATE = "DELETE FROM %s";
    private static final String WHERE_CLAUSE_TEMPLATE = " WHERE %s = %s";

    private static class InstanceHolder {
        private static final DeleteQueryBuilder INSTANCE = new DeleteQueryBuilder();
    }

    public static DeleteQueryBuilder getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public String build(Table table, IdColumn idColumn, Object id) {
        String deleteQuery = String.format(DELETE_QUERY_TEMPLATE, table.getName());
        String whereClause = String.format(WHERE_CLAUSE_TEMPLATE, idColumn.getName(), id);
        return deleteQuery + whereClause;
    }
}
