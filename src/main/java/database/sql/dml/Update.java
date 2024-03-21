package database.sql.dml;

import database.mapping.column.GeneralEntityColumn;
import database.mapping.column.PrimaryKeyEntityColumn;
import database.sql.dml.part.ValueMap;
import database.sql.dml.part.WhereClause;
import database.sql.dml.part.WhereMap;

import java.util.List;
import java.util.StringJoiner;

import static database.sql.Util.quote;

public class Update {
    private final String tableName;
    private final List<GeneralEntityColumn> generalColumns;
    private final PrimaryKeyEntityColumn primaryKey;
    private ValueMap changes;
    private WhereClause where;

    public Update(String tableName, List<GeneralEntityColumn> generalColumns, PrimaryKeyEntityColumn primaryKey) {
        this.tableName = tableName;
        this.generalColumns = generalColumns;
        this.primaryKey = primaryKey;
        this.changes = null;
        this.where = null;
    }

    public Update changes(ValueMap from) {
        this.changes = from;
        return this;
    }

    public Update changesFromEntity(Object entity) {
        return this.changes(ValueMap.fromEntity(entity, generalColumns));
    }

    public Update byId(long id) {
        this.where = WhereClause.from(
                WhereMap.of(primaryKey.getColumnName(), id),
                List.of(primaryKey.getColumnName()))
        ;
        return this;
    }

    public String buildQuery() {
        return String.format("UPDATE %s SET %s %s",
                             tableName,
                             setClauses(),
                             where.toQuery());
    }

    private String setClauses() {
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