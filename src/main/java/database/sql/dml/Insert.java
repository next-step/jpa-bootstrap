package database.sql.dml;

import database.mapping.column.GeneralEntityColumn;
import database.mapping.column.PrimaryKeyEntityColumn;
import database.sql.dml.part.ValueMap;
import persistence.entity.context.PersistentClass;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static database.sql.Util.quote;

public class Insert {
    private final String tableName;
    private final List<GeneralEntityColumn> generalColumns;
    private final PrimaryKeyEntityColumn primaryKey;

    public static <T> Insert from(PersistentClass<T> persistentClass) {
        return new Insert(
                persistentClass.getTableName(),
                persistentClass.getPrimaryKey(),
                persistentClass.getGeneralColumns());
    }

    public Insert(String tableName, PrimaryKeyEntityColumn primaryKey, List<GeneralEntityColumn> generalColumns) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.generalColumns = generalColumns;
    }

    public String toSqlFromEntity(Long id, Object entity) {
        return toSql(id, ValueMap.fromEntity(entity, generalColumns));
    }

    public String toSql(Long id, ValueMap values) {
        if (values == null) throw new RuntimeException("values are required");

        boolean includeIdField = id != null;
        return String.format("INSERT INTO %s (%s) VALUES (%s)",
                             tableName,
                             columnClauses(includeIdField, values),
                             valueClauses(includeIdField, id, values));
    }

    private List<String> columns(ValueMap valueMap) {
        return generalColumns.stream()
                .map(GeneralEntityColumn::getColumnName)
                .filter(valueMap::containsKey)
                .collect(Collectors.toList());
    }

    private String columnClauses(boolean includeIdField, ValueMap values) {
        StringJoiner joiner = new StringJoiner(", ");
        if (includeIdField) {
            joiner.add(primaryKey.getColumnName());
        }
        columns(values).forEach(joiner::add);
        return joiner.toString();
    }

    private String valueClauses(boolean includeIdField, Long id, ValueMap values) {
        StringJoiner joiner = new StringJoiner(", ");
        if (includeIdField) {
            joiner.add(quote(id));
        }
        columns(values).forEach(key -> joiner.add(quote(values.get(key))));
        return joiner.toString();
    }
}
