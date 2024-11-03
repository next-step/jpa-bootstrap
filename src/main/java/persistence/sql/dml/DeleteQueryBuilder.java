package persistence.sql.dml;

import static persistence.sql.QueryConst.*;

public class DeleteQueryBuilder {
    private final StringBuilder sql;

    public DeleteQueryBuilder() {
        this.sql = new StringBuilder();
    }

    public DeleteQueryBuilder deleteFrom(String tableName) {
        sql.append(DELETE_FROM_CLAUSE)
                .append(BLANK)
                .append(tableName)
                .append(BLANK);
        return this;
    }

    public DeleteQueryBuilder where(String column, Object value) {
        sql.append(WHERE_CLAUSE)
                .append(BLANK)
                .append(column)
                .append(EQUAL)
                .append(getValueWithQuotes(value));
        return this;
    }

    public String build() {
        return sql.toString().trim();
    }

    private String getValueWithQuotes(Object value) {
        if (value.getClass() == String.class) {
            return "'%s'".formatted(String.valueOf(value));
        }
        return String.valueOf(value);
    }
}
