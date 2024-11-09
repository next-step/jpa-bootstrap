package jdbc.mapping;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface FieldMapping {
    <T> boolean supports(Class<T> entityType);

    <T> T getRow(ResultSet resultSet, Class<T> entityType) throws SQLException, IllegalAccessException;

    default void mapField(ResultSet resultSet, Object entity, Field field, int columnIndex) throws SQLException, IllegalAccessException {
        final Object value = resultSet.getObject(columnIndex);
        field.setAccessible(true);
        field.set(entity, value);
    }
}
