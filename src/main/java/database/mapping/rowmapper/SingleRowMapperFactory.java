package database.mapping.rowmapper;

import database.dialect.Dialect;
import jdbc.RowMapper;
import persistence.entity.context.PersistentClass;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SingleRowMapperFactory {
    public static <T> RowMapper<T> create(PersistentClass<T> persistentClass, Dialect dialect) {
        return resultSet -> {
            ResultSetMetaData rsMetaData = resultSet.getMetaData();
            T object = persistentClass.newEntity();

            for (int i = 1; i < rsMetaData.getColumnCount() + 1; i++) {
                String columnName = rsMetaData.getColumnName(i);
                int columnType = rsMetaData.getColumnType(i);
                setFieldValue(resultSet, columnName, i, columnType, object, persistentClass, dialect);
            }
            return object;
        };
    }

    private SingleRowMapperFactory() {
    }

    private static <T> void setFieldValue(ResultSet resultSet,
                                          String columnName,
                                          int columnIndex,
                                          int columnType,
                                          Object entity,
                                          PersistentClass<T> persistentClass,
                                          Dialect dialect) throws SQLException {
        Field field = persistentClass.getFieldByColumnName(columnName);
        Object value = dialect.getFieldValueFromResultSet(resultSet, columnIndex, columnType);

        field.setAccessible(true);
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
