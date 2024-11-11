package jdbc.mapping;

import persistence.entity.manager.factory.InstanceFactory;
import persistence.meta.EntityTable;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleFieldMapping implements FieldMapping {
    @Override
    public boolean supports(EntityTable entityTable) {
        return entityTable.isSimpleMapping();
    }

    @Override
    public Object getRow(ResultSet resultSet, EntityTable entityTable) throws SQLException, IllegalAccessException {
        final Object entity = new InstanceFactory<>(entityTable.getType()).createInstance();
        final AtomicInteger fieldIndex = new AtomicInteger(0);

        for (int i = 0; i < getColumnCount(resultSet); i++) {
            Field field = getField(entityTable.getFields(), fieldIndex);
            if (Objects.nonNull(field)) {
                mapField(resultSet, entity, field, i + 1);
            }
        }
        return entity;
    }

    private int getColumnCount(ResultSet resultSet) throws SQLException {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        return metaData.getColumnCount();
    }

    private Field getField(List<Field> fields, AtomicInteger fieldIndex) {
        while (fieldIndex.get() < fields.size()) {
            final Field field = fields.get(fieldIndex.getAndAdd(1));
            if (field.getType() != List.class) {
                return field;
            }
        }
        return null;
    }
}
