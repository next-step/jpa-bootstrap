package persistence.sql.dml.query;

import persistence.sql.definition.TableDefinition;

public class DeleteQueryBuilder {

    public String build(Object entity, TableDefinition tableDefinition) {
        final StringBuilder query = new StringBuilder();

        query.append("DELETE FROM ");
        query.append(tableDefinition.getTableName());

        query.append(" WHERE ");
        query.append(tableDefinition.getIdColumnName())
                .append(" = ")
                .append(tableDefinition.getIdValue(entity)).append(";");
        return query.toString();
    }
}
