package persistence.sql.dml.query;

public class SelectAllQueryBuilder {
    public String build(String tableName) {
        final StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM ")
                .append(tableName)
                .append(";");
        return query.toString();
    }
}
