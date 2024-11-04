package persistence.sql.ddl.query;

import persistence.meta.Metamodel;
import persistence.sql.definition.TableDefinition;

public class DropQueryBuilder {
    private final StringBuilder query;

    public DropQueryBuilder(Class<?> entityClass, Metamodel metamodel) {
        query = new StringBuilder();
        TableDefinition tableDefinition = metamodel.getTableDefinition(entityClass);

        query.append("DROP TABLE ");
        query.append(tableDefinition.getTableName());
        query.append(" if exists;");
    }

    public String build() {
        return query.toString();
    }
}
