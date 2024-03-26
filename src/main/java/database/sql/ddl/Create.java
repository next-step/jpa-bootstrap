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

    private String sql;

    public static Create from(PersistentClass<?> persistentClass, Metadata metadata, Dialect dialect) {
        Create create = new Create(
                persistentClass.getTableName(),
                persistentClass.getPrimaryKey(),
                persistentClass.getGeneralColumns(),
                metadata.getAssociationsRelatedTo(persistentClass),
                metadata,
                dialect
        );
        create.buildSql();
        return create;
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

        this.sql = null;
    }

    private void buildSql() {
        ColumnsBuilder columnsBuilder = new ColumnsBuilder();
        columnsBuilder.add(primaryKey.getColumnName(), getPrimaryKeyColumnDefinition());
        for (GeneralEntityColumn generalEntityColumn : generalColumns) {
            columnsBuilder.add(generalEntityColumn.getColumnName(), getGeneralColumnDefinition(generalEntityColumn));
        }
        for (Association association : associationRelatedToOtherEntities) {
            columnsBuilder.add(association.getForeignKeyColumnName(), getAssociationFieldDefinition(association));
        }

        this.sql = String.format(dialect.createTableClause(), tableName, String.join(", ", columnsBuilder.toList()));
    }

    private String getPrimaryKeyColumnDefinition() {
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
        return sql;
    }
}
