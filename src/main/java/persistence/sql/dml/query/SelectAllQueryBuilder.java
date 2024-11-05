package persistence.sql.dml.query;

import persistence.meta.Metamodel;
import persistence.sql.definition.TableDefinition;

public class SelectAllQueryBuilder {
    public String build(Class<?> entityClass, Metamodel metamodel) {
        final StringBuilder query = new StringBuilder();
        final TableDefinition tableDefinition = metamodel.findTableDefinition(entityClass);

        query.append("SELECT * FROM ");
        query.append(tableDefinition.getTableName());
        query.append(";");
        return query.toString();
    }
}
