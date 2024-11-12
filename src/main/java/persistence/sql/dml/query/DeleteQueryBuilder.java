package persistence.sql.dml.query;

public class DeleteQueryBuilder {

    public String build(String tableName,
                        String idColumnName,
                        Object idValue) {

        return "DELETE FROM " +
                tableName +
                " WHERE " +
                idColumnName +
                " = " +
                idValue + ";";
    }
}
