package database.sql.dml;

import database.mapping.column.GeneralEntityColumn;
import database.mapping.column.PrimaryKeyEntityColumn;
import database.sql.dml.part.ValueMap;
import database.sql.dml.part.WhereClause;
import database.sql.dml.part.WhereMap;
import persistence.entity.context.PersistentClass;

import java.util.List;
import java.util.StringJoiner;

import static database.sql.Util.quote;

public class Update {
    private final String tableName;
    private final List<GeneralEntityColumn> generalColumns;
    private final PrimaryKeyEntityColumn primaryKey;

    public static <T> Update from(PersistentClass<T> persistentClass) {
        return new Update(
                persistentClass.getTableName(),
                persistentClass.getGeneralColumns(),
                persistentClass.getPrimaryKey());
    }

    private Update(String tableName, List<GeneralEntityColumn> generalColumns, PrimaryKeyEntityColumn primaryKey) {
        this.tableName = tableName;
        this.generalColumns = generalColumns;
        this.primaryKey = primaryKey;
    }

    public String toSql(ValueMap changes, Long id) {
        WhereClause where = WhereClause.from(
                WhereMap.of(primaryKey.getColumnName(), id),
                List.of(primaryKey.getColumnName()));
        return String.format("UPDATE %s SET %s %s",
                             tableName,
                             setClauses(changes),
                             where.toQuery());
    }

    public String toSqlFromEntity(Object entity, Long id) {
        return toSql(ValueMap.fromEntity(entity, generalColumns), id);
    }

    private String setClauses(ValueMap changes) {
        StringJoiner joiner = new StringJoiner(", ");
        for (GeneralEntityColumn generalColumn : generalColumns) {
            String columnName = generalColumn.getColumnName();

            if (changes.containsKey(columnName)) {
                joiner.add(String.format("%s = %s", columnName, quote(changes.get(columnName))));
            }
        }
        return joiner.toString();
    }
}
