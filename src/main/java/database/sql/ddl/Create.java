package database.sql.ddl;

import database.dialect.Dialect;
import database.mapping.Association;
import database.mapping.column.GeneralEntityColumn;
import database.mapping.column.PrimaryKeyEntityColumn;
import persistence.bootstrap.Metadata;
import database.mapping.EntityColumns;
import persistence.entity.context.PersistentClass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Create {
    private final String tableName;
    private final Dialect dialect;
    private final Metadata metadata;

    private final PrimaryKeyEntityColumn primaryKey;
    private final List<GeneralEntityColumn> generalColumns;
    private final List<Association> associationColumns;

    public static Create from(PersistentClass<?> persistentClass, Metadata metadata, Dialect dialect) {
        EntityColumns entityColumns = persistentClass.getEntityColumns(metadata.getEntityClasses());
        return new Create(
                persistentClass.getTableName(),
                metadata,
                dialect,
                entityColumns.getPrimaryKey(),
                entityColumns.getGeneralColumns(),
                entityColumns.getAssociationColumns()
        );
    }

    private Create(
            String tableName,
            Metadata metadata,
            Dialect dialect,
            PrimaryKeyEntityColumn primaryKey,
            List<GeneralEntityColumn> generalColumns,
            List<Association> associationColumns) {
        this.tableName = tableName;
        this.metadata = metadata;
        this.dialect = dialect;
        this.primaryKey = primaryKey;
        this.generalColumns = generalColumns;
        this.associationColumns = associationColumns;
    }

    private String getPrimaryKeyColumnDefinition(PrimaryKeyEntityColumn primaryKey) {
        StringJoiner definitionJoiner = new StringJoiner(" ");
        definitionJoiner.add(primaryKey.getColumnName());

        definitionJoiner.add(dialect.convertToSqlTypeDefinition(primaryKey.getType(), primaryKey.getColumnLength()));

        if (primaryKey.isAutoIncrement()) {
            definitionJoiner.add(dialect.autoIncrementDefinition());
        }
        definitionJoiner.add(dialect.primaryKeyDefinition());

        return definitionJoiner.toString();
    }

    private String getGeneralColumnDefinition(GeneralEntityColumn entityColumn) {
        return String.format(
                "%s %s %s",
                entityColumn.getColumnName(),
                dialect.convertToSqlTypeDefinition(
                        entityColumn.getType(),
                        entityColumn.getColumnLength()),
                entityColumn.isNullable() ? dialect.nullableDefinition() : dialect.notNullDefinition());
    }

    private String getAssociationFieldDefinition(Association associationColumn) {
        return String.format(
                "%s %s %s",
                associationColumn.getForeignKeyColumnName(),
                associationColumn.getForeignKeyColumnType(metadata, dialect),
                dialect.notNullDefinition());
    }

    public String toSql() {
        List<String> list = new ArrayList<>();

        list.add(getPrimaryKeyColumnDefinition(primaryKey));
        for (GeneralEntityColumn generalEntityColumn : generalColumns) {
            list.add(getGeneralColumnDefinition(generalEntityColumn));
        }
        for (Association association : associationColumns) {
            list.add(getAssociationFieldDefinition(association));
        }

        return String.format(dialect.createTableClause(), tableName, String.join(", ", list));
    }
}
