package database.sql.ddl;

import persistence.entity.context.PersistentClass;

public class Drop {
    private static final String DROP_IF_EXISTS = "DROP TABLE %s IF EXISTS";
    private static final String DROP = "DROP TABLE %s";

    private final String tableName;

    public static <T> Drop from(PersistentClass<T> persistentClass) {
        return new Drop(persistentClass.getTableName());
    }

    private Drop(String tableName) {
        this.tableName = tableName;
    }

    public String toSql(boolean ifExists) {
        if (ifExists) {
            return String.format(DROP_IF_EXISTS, tableName);
        }
        return String.format(DROP, tableName);
    }
}
