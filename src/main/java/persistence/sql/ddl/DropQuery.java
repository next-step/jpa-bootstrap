package persistence.sql.ddl;

public class DropQuery {
    private static final String QUERY_TEMPLATE = "DROP TABLE IF EXISTS %s";

    private final String tableName;

    public DropQuery(String tableName) {
        this.tableName = tableName;
    }

    public String drop() {
        return QUERY_TEMPLATE.formatted(tableName);
    }
}
