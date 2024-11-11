package jdbc.mapping;

import persistence.meta.EntityTable;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface FieldMapping {
    boolean supports(EntityTable entityTable);

    Object getRow(ResultSet resultSet, EntityTable entityTable) throws SQLException, IllegalAccessException;

    default void mapField(ResultSet resultSet, Object entity, Field field, int columnIndex) throws SQLException, IllegalAccessException {
        final Object value = resultSet.getObject(columnIndex);
        field.setAccessible(true);
        field.set(entity, value);
    }
}
