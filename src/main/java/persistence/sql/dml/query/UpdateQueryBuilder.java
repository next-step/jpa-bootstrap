package persistence.sql.dml.query;

import common.SqlLogger;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class UpdateQueryBuilder implements BaseQueryBuilder {
    public String build(String tableName,
                        Serializable identifierKey,
                        Object identifierValue,
                        Map<String, Object> columns) {

        final StringBuilder query = new StringBuilder("UPDATE ").append(tableName);
        columnClause(
                query,
                columns
        );

        byId(identifierKey, identifierValue, query);

        final String updateQuery = query.toString();
        SqlLogger.infoUpdate(updateQuery);
        return updateQuery;
    }

    private void columnClause(StringBuilder query, Map<String, Object> columns) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("Columns cannot be null or empty");
        }

        query.append(" SET ");
        String columnClause = columns.entrySet().stream()
                .map(entry -> entry.getKey() + " = " + entry.getValue())
                .reduce((column1, column2) -> column1 + ", " + column2).orElse("");
        query.append(columnClause);
    }

    private void byId(Serializable identifierKey, Object identifierValue, StringBuilder query) {
        query.append(" WHERE ");
        query.append(identifierKey)
                .append(" = ")
                .append(identifierValue)
                .append(";");
    }
}
