package database.sql.ddl;

import persistence.entity.context.PersistentClass;

public class Drop<T> {
    private final String tableName;

    public static <T> Drop<T> from(PersistentClass<T> persistentClass) {
        return new Drop<>(persistentClass.getTableName());
    }

    private Drop(String tableName) {
        this.tableName = tableName;
    }

    public String buildQuery() {
        return String.format("DROP TABLE %s", tableName);
    }
}
