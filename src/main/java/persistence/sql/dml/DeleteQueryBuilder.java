package persistence.sql.dml;

import persistence.dialect.Dialect;
import persistence.exception.FieldEmptyException;
import persistence.meta.EntityColumn;
import persistence.meta.EntityColumns;
import persistence.meta.TableName;

public class DeleteQueryBuilder extends DMLQueryBuilder {

    public DeleteQueryBuilder(Dialect dialect) {
        super(dialect);
    }

    public String build(Class<?> clazz, Object id) {
        if (id == null) {
            throw new FieldEmptyException("id가 비어 있으면 안 됩니다.");
        }

        final TableName tableName = TableName.from(clazz);
        final EntityColumn pkColumn = EntityColumns.from(clazz).pkColumn();

        return getDeleteQuery()
                + getFromTableQuery(tableName.getValue())
                + whereId(pkColumn, id);
    }

    private String getDeleteQuery() {
        return dialect.deleteQuery();
    }
}
