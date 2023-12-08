package persistence.sql.dml;

import java.util.List;
import java.util.stream.Collectors;
import persistence.dialect.Dialect;
import persistence.meta.EntityColumn;
import persistence.meta.EntityColumns;
import persistence.meta.TableName;


public class UpdateQueryBuilder extends DMLQueryBuilder {

    private static final String EQUAL = "=";

    public UpdateQueryBuilder(Dialect dialect) {
        super(dialect);
    }

    public String build(Object entity) {
        final TableName tableName = TableName.from(entity.getClass());
        final EntityColumns entityColumns = EntityColumns.from(entity.getClass());

        return updateQuery(tableName.getValue())
                + updateValues(entityColumns.getEntityColumns(), entity)
                + whereId(entityColumns.pkColumn(), entityColumns.pkColumn().getFieldValue(entity));
    }

    private String updateQuery(String tableName) {
        return dialect.updateForTableQuery(tableName);
    }

    private String updateValues(List<EntityColumn> entityColumns, Object entity) {
        return entityColumns
                .stream()
                .filter((it) -> !it.isPk())
                .map((it) -> it.getName() + EQUAL + getColumnValueString(it, entity))
                .collect(Collectors.joining(", "));
    }

}
