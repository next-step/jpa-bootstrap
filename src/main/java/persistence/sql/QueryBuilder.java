package persistence.sql;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import persistence.dialect.Dialect;


public abstract class QueryBuilder {
    protected static final String DEFAULT_COLUMNS_BRACE = " (%s)";
    protected static final String MARGIN = " ";
    protected static final String EMPTY = "";

    protected final Dialect dialect;

    protected QueryBuilder(Dialect dialect) {
        if (dialect == null) throw new IllegalArgumentException("dialect는 필수 입니다.");
        this.dialect = dialect;
    }

    protected String brace(String... query) {
        return brace(Arrays.asList(query));
    }

    protected String brace(Collection<String> query) {
        return String.format(DEFAULT_COLUMNS_BRACE, combinedString(query));
    }

    protected String braceWithComma(Collection<String> values) {
        return String.format(DEFAULT_COLUMNS_BRACE, String.join(", ", values));
    }

    protected String combinedString(Collection<String> queries) {
        return queries.stream()
                .filter(it -> !it.isBlank())
                .collect(Collectors.joining(EMPTY));
    }

    protected String combinedQuery(Collection<String> queries) {
        return queries.stream()
                .filter(it -> !it.isBlank())
                .collect(Collectors.joining(MARGIN));
    }

    protected String combinedQuery(String... queries) {
        return combinedQuery(Arrays.asList(queries));
    }

    protected String columnSignature(String tableNameSignature, String columnName) {
        return tableNameSignature + "." + columnName;
    }

    protected String tableNameSignature(String tableName) {
        return tableNameSignature(tableName, 0);
    }

    protected String tableNameSignature(String tableName, int depth) {
        return tableName + "_" + depth;
    }

}
