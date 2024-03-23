package database.mapping.rowmapper;

import database.mapping.column.EntityColumn;
import persistence.entity.context.PersistentClass;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

// TODO: 테스트 추가
public class JoinedRow<T> {
    private final PersistentClass<T> persistentClass;
    private final Map<String, Object> map;

    public JoinedRow(PersistentClass<T> persistentClass) {
        this.persistentClass = persistentClass;
        this.map = new HashMap<>();
    }

    public void add(String tableName, String columnName, Object value) {
        map.put(mapKey(tableName, columnName), value);
    }

    public T getOwnerEntity() {
        return mapValues(persistentClass);
    }

    public <R> R mapValues(PersistentClass<R> persistentClass) {
        R entity = persistentClass.newInstance();
        for (EntityColumn allEntityColumn : persistentClass.getAllEntityColumns()) {
            setField(entity, allEntityColumn.getColumnName());
        }
        return entity;
    }

    private <R> void setField(R entity, String columnName) {
        PersistentClass<?> persistentClass = PersistentClass.from(entity.getClass());
        String tableName = persistentClass.getTableName();
        Object value = map.get(mapKey(tableName, columnName));

        setFieldValue(entity, columnName, value);
    }

    private <R> void setFieldValue(R entity, String columnName, Object value) {
        PersistentClass<?> persistentClass = PersistentClass.from(entity.getClass());

        Field field = persistentClass.getFieldByColumnName(columnName);
        field.setAccessible(true);
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static String mapKey(String tableName, String columnName) {
        return (tableName + "." + columnName).toUpperCase();
    }
}
