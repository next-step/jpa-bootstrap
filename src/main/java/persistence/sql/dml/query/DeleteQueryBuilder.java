package persistence.sql.dml.query;

import persistence.meta.Metamodel;
import persistence.sql.definition.TableDefinition;

public class DeleteQueryBuilder {

    public String build(Object entity, Metamodel metamodel) {
        final StringBuilder query = new StringBuilder();
        final TableDefinition tableDefinition = metamodel.getTableDefinition(entity.getClass());

        query.append("DELETE FROM ");
        query.append(tableDefinition.getTableName());

        query.append(" WHERE ");
        query.append(tableDefinition.getIdColumnName())
                .append(" = ")
                .append(tableDefinition.getIdValue(entity)).append(";");
        return query.toString();
    }
}
