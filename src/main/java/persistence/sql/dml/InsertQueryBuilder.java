package persistence.sql.dml;

import java.util.List;
import java.util.stream.Collectors;

import static persistence.sql.QueryConst.*;

public class InsertQueryBuilder {
    private final StringBuilder sql;

    public InsertQueryBuilder() {
        this.sql = new StringBuilder();
    }

    public InsertQueryBuilder insertInto(String tableName, List<String> columns) {
        sql.append(INSERT_INTO_CLAUSE)
                .append(BLANK)
                .append(tableName)
                .append(LEFT_PARENTHESES)
                .append(getColumnClause(columns))
                .append(RIGHT_PARENTHESES);
        return this;
    }

    public InsertQueryBuilder values(List<Object> values) {
        sql.append(VALUES_CLAUSE)
                .append(LEFT_PARENTHESES)
                .append(getValueClause(values))
                .append(RIGHT_PARENTHESES);
        return this;
    }

    public String build() {
        return sql.toString().trim();
    }

    private String getColumnClause(List<String> columns) {
        return String.join(COLUMN_DELIMITER, columns);
    }

    private String getValueClause(List<Object> values) {
        return values.stream()
                .map(this::getValueWithQuotes)
                .collect(Collectors.joining(COLUMN_DELIMITER));
    }

    private String getValueWithQuotes(Object value) {
        if (value.getClass() == String.class) {
            return "'%s'".formatted(String.valueOf(value));
        }
        return String.valueOf(value);
    }
}
