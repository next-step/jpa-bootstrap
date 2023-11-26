package persistence.entity;

import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.JoinColumn;
import persistence.sql.common.meta.TableName;

public class EntityMeta {
    private final TableName tableName;
    private final Columns columns;
    private String methodName;
    private JoinColumn joinColumn;

    private EntityMeta(TableName tableName, Columns columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public static EntityMeta make(TableName tableName, Columns columns) {
        return new EntityMeta(tableName, columns);
    }

    public static EntityMeta makeWithJoinColumn(TableName tableName, Columns columns, JoinColumn joinColumn) {
        EntityMeta entityMeta = new EntityMeta(tableName, columns);

        entityMeta.joinColumn = joinColumn;

        return entityMeta;
    }

    public static EntityMeta makeWithJoinColumn(String methodName, TableName tableName, Columns columns, JoinColumn joinColumn) {
        EntityMeta entityMeta = new EntityMeta(tableName, columns);

        entityMeta.methodName = methodName;
        entityMeta.joinColumn = joinColumn;

        return entityMeta;
    }

    public static EntityMeta makeWithMethodName(String methodName, TableName tableName, Columns columns) {
        EntityMeta entityMeta = new EntityMeta(tableName, columns);

        entityMeta.methodName = methodName;

        return entityMeta;
    }

    public String getMethodName() {
        return methodName;
    }

    public TableName getTableName() {
        return tableName;
    }

    public Columns getColumns() {
        return columns;
    }

    public JoinColumn getJoinColumn() {
        return joinColumn;
    }
}
