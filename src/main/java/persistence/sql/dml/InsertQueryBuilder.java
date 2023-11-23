package persistence.sql.dml;

import jakarta.persistence.Entity;
import java.util.List;
import java.util.stream.Collectors;
import persistence.dialect.Dialect;
import persistence.exception.NoEntityException;
import persistence.meta.EntityColumn;
import persistence.meta.EntityColumns;
import persistence.meta.TableName;

public class InsertQueryBuilder extends DMLQueryBuilder {

    public InsertQueryBuilder(Dialect dialect) {
        super(dialect);
    }

    public String build(Class<?> entityClass, Object queryValue) {
        if (entityClass == null || entityClass.getAnnotation(Entity.class) == null) {
            throw new NoEntityException();
        }

        final EntityColumns entityColumns = EntityColumns.from(entityClass);
        pkColumValidate(entityColumns, queryValue);

        return queryInsert(TableName.from(entityClass).getValue())
                + braceWithComma(columnsClause(entityColumns.getEntityColumns()))
                + values(valueClause(entityColumns, queryValue));
    }


    private List<String> columnsClause(List<EntityColumn> entityColumns) {
        return entityColumns
                .stream()
                .filter(column -> !column.hasGeneratedValue())
                .map(EntityColumn::getName)
                .collect(Collectors.toList());
    }

    private String valueClause(EntityColumns entityColumns, Object value) {
        return entityColumns.getEntityColumns()
                .stream()
                .filter(column -> !column.hasGeneratedValue())
                .map(column -> getColumnValueString(column, value))
                .collect(Collectors.joining(", "));
    }

    private String queryInsert(String tableName) {
        return dialect.insert(tableName);
    }

    private String values(String value) {
        return dialect.valuesQuery(value);
    }

    private void pkColumValidate(EntityColumns entityColumns, Object queryValue) {
        final EntityColumn pkColumn = entityColumns.pkColumn();

        if (!pkColumn.hasGeneratedValue() && pkColumn.getFieldValue(queryValue) == null) {
            throw new IllegalArgumentException("pk가 없습니다.");
        }
    }
}
