package persistence.sql.dml;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static persistence.sql.QueryConst.*;

public class UpdateQueryBuilder {
    private final StringBuilder sql;

    public UpdateQueryBuilder() {
        this.sql = new StringBuilder();
    }

    public UpdateQueryBuilder update(String tableName) {
        sql.append(UPDATE_CLAUSE)
                .append(BLANK)
                .append(tableName)
                .append(BLANK);
        return this;
    }

    public UpdateQueryBuilder set(List<String> columns, List<Object> values) {
        final String setClause = IntStream.range(0, columns.size())
                .mapToObj(i -> equalClause(columns.get(i), values.get(i)))
                .collect(Collectors.joining(COLUMN_DELIMITER));

        sql.append(SET_CLAUSE)
                .append(BLANK)
                .append(setClause)
                .append(BLANK);
        return this;
    }

    public UpdateQueryBuilder where(String column, Object value) {
        sql.append(WHERE_CLAUSE)
                .append(BLANK)
                .append(equalClause(column, value));
        return this;
    }

    public String build() {
        return sql.toString().trim();
    }

    private String equalClause(String column, Object value) {
        StringBuilder sql = new StringBuilder();
        sql.append(column)
                .append(EQUAL)
                .append(getValueWithQuotes(value));
        return sql.toString();
    }

    private String getValueWithQuotes(Object value) {
        if (value.getClass() == String.class) {
            return "'%s'".formatted(String.valueOf(value));
        }
        return String.valueOf(value);
    }
}
