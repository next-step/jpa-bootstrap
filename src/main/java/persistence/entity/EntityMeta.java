package persistence.entity;

import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.JoinColumn;
import persistence.sql.common.meta.TableName;

public class EntityMeta {
    private final TableName tableName;
    private final Columns columns;
    private String methodName;
    private JoinColumn joinColumn;

    public EntityMeta(TableName tableName, Columns columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public EntityMeta(String methodName, TableName tableName, Columns columns, JoinColumn joinColumn) {
        this.methodName = methodName;
        this.tableName = tableName;
        this.columns = columns;
        this.joinColumn = joinColumn;
    }

    public EntityMeta(String methodName, TableName tableName, Columns columns) {
        this.methodName = methodName;
        this.tableName = tableName;
        this.columns = columns;
    }

    public EntityMeta(TableName tableName, Columns columns, JoinColumn joinColumn) {
        this.tableName = tableName;
        this.columns = columns;
        this.joinColumn = joinColumn;
    }

    public static EntityMeta selectMeta(String methodName, TableName tableName, Columns columns,
        JoinColumn joinColumn) {
        return new EntityMeta(methodName, tableName, columns, joinColumn);
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
