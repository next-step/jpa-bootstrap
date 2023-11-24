package persistence.sql.ddl;

import jakarta.persistence.Entity;
import java.util.List;
import java.util.stream.Collectors;
import persistence.dialect.Dialect;
import persistence.exception.NoEntityException;
import persistence.meta.ColumnType;
import persistence.meta.EntityColumn;
import persistence.meta.EntityColumns;
import persistence.meta.TableName;
import persistence.sql.QueryBuilder;


public class CreateQueryBuilder extends QueryBuilder {

    public CreateQueryBuilder(Dialect dialect) {
        super(dialect);
    }

    public String build(Class<?> clazz) {
        if (clazz == null || clazz.getAnnotation(Entity.class) == null) {
            throw new NoEntityException();
        }

        final TableName tableName = TableName.from(clazz);
        final EntityColumns entityColumns = EntityColumns.from(clazz);

        return queryCreate(tableName.getValue())
                + brace(columnsCreateQuery(entityColumns.getEntityColumns())
                , primaryKeyConcentrate(entityColumns.getEntityColumns()));
    }

    private String queryCreate(String tableName) {
        return dialect.createTablePreFix(tableName);
    }

    private String columnQuery(EntityColumn entityColumn) {
        return combinedQuery(" ",
                entityColumn.getName(),
                columnTypeQuery(entityColumn),
                notNullQuery(entityColumn),
                generatedTypeQuery(entityColumn)
        );
    }

    private String columnTypeQuery(EntityColumn entityColumn) {
        final ColumnType columType = entityColumn.getColumnType();
        if (columType.isVarchar()) {
            return dialect.getVarchar(entityColumn.getLength());
        }
        return dialect.getColumnType(columType);
    }

    private String notNullQuery(EntityColumn entityColumn) {
        if (entityColumn.isNotNull()) {
            return dialect.notNull();
        }
        return "";
    }

    private String generatedTypeQuery(EntityColumn entityColumn) {
        return dialect.getGeneratedType(entityColumn.getGenerationType());
    }

    private String primaryKeyConcentrate(List<EntityColumn> entityColumns) {
        return dialect.primaryKey(entityColumns.stream()
                .filter(EntityColumn::isPk)
                .map(EntityColumn::getName)
                .collect(Collectors.joining(", ")));
    }

    private String columnsCreateQuery(List<EntityColumn> entityColumns) {
        return entityColumns
                .stream()
                .map(this::columnQuery)
                .collect(Collectors.joining(", ", "", ", "));
    }
}
