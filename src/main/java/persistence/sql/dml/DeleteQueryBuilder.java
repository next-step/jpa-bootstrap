package persistence.sql.dml;

import persistence.sql.column.IdColumn;
import persistence.sql.column.TableColumn;

public class DeleteQueryBuilder implements DmlQueryBuilder {

    private static final String DELETE_QUERY_FORMAT = "delete from %s";
    private static final String WHERE_CLAUSE_FORMAT = " where %s = %d";

    private IdColumn idColumn;
    private String query;

    public DeleteQueryBuilder() {}

    public DeleteQueryBuilder build(Object entity) {
        Class<?> clazz = entity.getClass();
        TableColumn tableColumn = new TableColumn(clazz);
        this.idColumn = new IdColumn(entity);
        this.query = String.format(DELETE_QUERY_FORMAT, tableColumn.getName());
        return this;
    }

    @Override
    public String toStatementWithId(Object id) {
        return query + String.format(WHERE_CLAUSE_FORMAT, idColumn.getName(), id);
    }
}
