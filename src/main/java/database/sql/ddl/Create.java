package database.sql.ddl;

import database.dialect.Dialect;
import database.mapping.Association;
import database.mapping.column.GeneralEntityColumn;
import database.mapping.column.PrimaryKeyEntityColumn;
import persistence.bootstrap.Metadata;
import persistence.entity.context.PersistentClass;

import java.util.List;
import java.util.StringJoiner;

public class Create {
    private final String tableName;
    private final Dialect dialect;
    private final Metadata metadata;
    private final List<Association> associationRelatedToOtherEntities;
    private final PrimaryKeyEntityColumn primaryKey;
    private final List<GeneralEntityColumn> generalColumns;

    private final String autoIncrementClause;
    private final String primaryKeyClause;
    private final String nullableClause;
    private final String notNullClause;
    private final String createTableClause;

    public static Create from(PersistentClass<?> persistentClass, Metadata metadata, Dialect dialect) {
        return new Create(
                persistentClass.getTableName(),
                persistentClass.getPrimaryKey(),
                persistentClass.getGeneralColumns(),
                metadata.getAssociationsRelatedTo(persistentClass),
                metadata,
                dialect
        );
    }

    private Create(
            String tableName,
            PrimaryKeyEntityColumn primaryKey,
            List<GeneralEntityColumn> generalColumns,
            List<Association> associationRelatedToOtherEntities,
            Metadata metadata,
            Dialect dialect) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.generalColumns = generalColumns;
        this.associationRelatedToOtherEntities = associationRelatedToOtherEntities;

        this.metadata = metadata;

        this.dialect = dialect;
        this.createTableClause = dialect.createTableClause();
        this.autoIncrementClause = dialect.autoIncrementDefinition();
        this.primaryKeyClause = dialect.primaryKeyDefinition();
        this.nullableClause = dialect.nullableDefinition();
        this.notNullClause = dialect.notNullDefinition();
    }

    public String buildQuery() {
        ColumnsBuilder columnsBuilder = new ColumnsBuilder();
        columnsBuilder.add(primaryKey.getColumnName(), getPrimaryKeyColumnDefinition(primaryKey));
        generalColumns.forEach(generalEntityColumn -> columnsBuilder.add(generalEntityColumn.getColumnName(), getGeneralColumnDefinition(generalEntityColumn)));
        associationRelatedToOtherEntities.forEach(association -> columnsBuilder.add(association.getForeignKeyColumnName(), getAssociationFieldDefinition(association)));

        return String.format(createTableClause, tableName, String.join(", ", columnsBuilder.toList()));
    }

    private String getPrimaryKeyColumnDefinition(PrimaryKeyEntityColumn entityColumn) {
        String columnName = entityColumn.getColumnName();
        Class<?> type = entityColumn.getType();
        Integer columnLength = entityColumn.getColumnLength();
        boolean autoIncrement = entityColumn.isAutoIncrement();

        StringJoiner definitionJoiner = new StringJoiner(" ");
        definitionJoiner.add(columnName);
        definitionJoiner.add(dialect.convertToSqlTypeDefinition(type, columnLength));

        if (autoIncrement) {
            definitionJoiner.add(autoIncrementClause);
        }
        definitionJoiner.add(primaryKeyClause);
        return definitionJoiner.toString();
    }

    private String getGeneralColumnDefinition(GeneralEntityColumn entityColumn) {
        String columnName = entityColumn.getColumnName();
        Class<?> type = entityColumn.getType();
        Integer columnLength = entityColumn.getColumnLength();
        String typeDefinition = dialect.convertToSqlTypeDefinition(type, columnLength);
        boolean nullable = entityColumn.isNullable();
        return String.format(
                "%s %s %s",
                columnName,
                typeDefinition,
                nullable ? nullableClause : notNullClause);
    }

    private String getAssociationFieldDefinition(Association associationColumn) {
        return String.format(
                "%s %s %s",
                associationColumn.getForeignKeyColumnName(),
                associationColumn.getForeignKeyColumnType(metadata, dialect),
                notNullClause);
    }
}
