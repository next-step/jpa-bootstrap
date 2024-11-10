package persistence.sql.ddl.query;

import persistence.meta.Metadata;
import persistence.sql.Dialect;
import persistence.sql.definition.ColumnDefinitionAware;
import persistence.sql.definition.TableDefinition;
import persistence.sql.definition.TableId;

import java.util.List;

public class CreateTableQueryBuilder {
    private final StringBuilder query;
    private final Dialect dialect;

    public CreateTableQueryBuilder(Class<?> entityClass, Metadata metadata) {
        TableDefinition tableDefinition = metadata.findTableDefinition(entityClass);
        this.query = new StringBuilder();
        this.dialect = metadata.getDialect();

        query.append("CREATE TABLE ").append(tableDefinition.getTableName());
        query.append(" (");

        columnClause(tableDefinition.getTableId(), tableDefinition.getColumns());

        List<? extends ColumnDefinitionAware> foreignKeys = metadata.getForeignKeys(entityClass);
        foreignKeys.forEach(column -> {
            appendColumn(column);
            query.append(", ");
        });

        definePrimaryKey(tableDefinition.getTableId(), query);

        query.append(");");
    }

    private void columnClause(TableId tableId, List<? extends ColumnDefinitionAware> columns) {
        for (ColumnDefinitionAware column : columns) {
            appendColumn(column);
            if (column.isPrimaryKey()) {
                query.append(tableId.generatePrimaryKeySQL());
            }
            query.append(", ");
        }
    }

    private void appendColumn(ColumnDefinitionAware columnDefinition) {
        final String type = dialect.translateType(columnDefinition);

        query.append(columnDefinition.getDatabaseColumnName())
                .append(" ")
                .append(type);

        if (!columnDefinition.isNullable()) {
            query.append(" NOT NULL");
        }
    }

    public String build() {
        return query.toString();
    }

    private void definePrimaryKey(TableId pk, StringBuilder query) {
        query.append("PRIMARY KEY (").append(pk.getDatabaseColumnName()).append(")");
    }
}
