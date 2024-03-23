package database.sql.ddl;

import persistence.entity.context.PersistentClass;

public class Drop<T> {
    private static final String DROP_IF_EXISTS = "DROP TABLE %s IF EXISTS";
    private static final String DROP = "DROP TABLE %s";

    private final String tableName;
    private boolean ifExists = false;

    public static <T> Drop<T> from(PersistentClass<T> persistentClass) {
        return new Drop<>(persistentClass.getTableName());
    }

    private Drop(String tableName) {
        this.tableName = tableName;
    }

    public Drop<T> ifExists(boolean ifExists) {
        this.ifExists = ifExists;
        return this;
    }

    public String buildQuery() {
        if (ifExists) {
            return String.format(DROP_IF_EXISTS, tableName);
        }
        return String.format(DROP, tableName);
    }
}
