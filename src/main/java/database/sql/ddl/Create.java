package database.sql.ddl;

import database.dialect.Dialect;
import database.mapping.Association;
import database.mapping.column.GeneralEntityColumn;
import database.mapping.column.PrimaryKeyEntityColumn;
import persistence.entity.context.PersistentClass;

import java.util.List;
import java.util.StringJoiner;

public class Create<T> {
    private final String tableName;
    private final Dialect dialect;
    private final List<Association> associationRelatedToOtherEntities;
    private final PrimaryKeyEntityColumn primaryKey;
    private final List<GeneralEntityColumn> generalColumns;

    public static <T> Create<T> from(PersistentClass<T> persistentClass, Dialect dialect) {
        return new Create<>(
                dialect,
                persistentClass.getTableName(),
                persistentClass.getPrimaryKey(),
                persistentClass.getGeneralColumns(),
                persistentClass.getAssociationsRelatedTo()
        );
    }

    private Create(Dialect dialect,
                   String tableName,
                   PrimaryKeyEntityColumn primaryKey,
                   List<GeneralEntityColumn> generalColumns,
                   List<Association> associationRelatedToOtherEntities) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.generalColumns = generalColumns;
        this.associationRelatedToOtherEntities = associationRelatedToOtherEntities;
        this.dialect = dialect;
    }

    public String buildQuery() {
        ColumnsBuilder columnsBuilder = new ColumnsBuilder();
        columnsBuilder.add(primaryKey.getColumnName(), getPrimaryKeyColumnDefinition(primaryKey));
        generalColumns.forEach(generalEntityColumn -> columnsBuilder.add(generalEntityColumn.getColumnName(), getGeneralColumnDefinition(generalEntityColumn)));
        associationRelatedToOtherEntities.forEach(association -> columnsBuilder.add(association.getForeignKeyColumnName(), getAssociationFieldDefinition(association)));

        return String.format("CREATE TABLE %s (%s)", tableName, String.join(", ", columnsBuilder.toList()));
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
            definitionJoiner.add(dialect.autoIncrementDefinition());
        }
        definitionJoiner.add(dialect.primaryKeyDefinition());
        return definitionJoiner.toString();
    }

    private String getGeneralColumnDefinition(GeneralEntityColumn entityColumn) {
        String columnName = entityColumn.getColumnName();
        Class<?> type = entityColumn.getType();
        Integer columnLength = entityColumn.getColumnLength();
        boolean nullable = entityColumn.isNullable();
        return String.format("%s %s %s",
                             columnName,
                             dialect.convertToSqlTypeDefinition(type, columnLength),
                             dialect.nullableDefinition(nullable));
    }

    private String getAssociationFieldDefinition(Association associationColumn) {
        return String.format("%s %s NOT NULL",
                             associationColumn.getForeignKeyColumnName(),
                             associationColumn.getForeignKeyColumnType(dialect));
    }
}
