package persistence.sql.ddl.query;

public class DropQueryBuilder {
    private final StringBuilder query;

    public DropQueryBuilder(String tableName) {
        query = new StringBuilder();

        query.append("DROP TABLE ");
        query.append(tableName);
        query.append(" if exists;");
    }

    public String build() {
        return query.toString();
    }
}
