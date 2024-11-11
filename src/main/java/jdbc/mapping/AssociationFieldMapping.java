package jdbc.mapping;

import jdbc.InstanceFactory;
import persistence.meta.EntityTable;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class AssociationFieldMapping implements FieldMapping {
    private final EntityTable childEntityTable;

    public AssociationFieldMapping(EntityTable childEntityTable) {
        this.childEntityTable = childEntityTable;
    }

    @Override
    public boolean supports(EntityTable entityTable) {
        return !entityTable.isSimpleMapping();
    }

    @Override
    public Object getRow(ResultSet resultSet, EntityTable entityTable) throws IllegalAccessException, SQLException {
        final Class<?> associationColumnType = entityTable.getAssociationColumnType();
        final Object entity = new InstanceFactory<>(entityTable.getType()).createInstance();
        final List<Object> collection = createCollection(entity, entityTable);

        do {
            final Object childEntity = createChildEntity(resultSet, associationColumnType, entity, entityTable);
            collection.add(childEntity);
        } while (resultSet.next());

        return entity;
    }

    private List<Object> createCollection(Object entity, EntityTable entityTable) throws IllegalAccessException {
        final Field collectionField = entityTable.getAssociationField();
        collectionField.setAccessible(true);
        Object collection = collectionField.get(entity);

        if (collection instanceof List<?>) {
            final List<Object> newCollection = new ArrayList<>((List<?>) collection);
            collectionField.set(entity, newCollection);
            return newCollection;
        }
        throw new ClassCastException();
    }

    private Object createChildEntity(ResultSet resultSet, Class<?> associationColumnType, Object entity, EntityTable entityTable) throws SQLException, IllegalAccessException {
        final Object childEntity = new InstanceFactory<>(associationColumnType).createInstance();
        final AtomicInteger fieldIndex = new AtomicInteger(0);
        final AtomicInteger childFieldIndex = new AtomicInteger(0);

        for (int i = 0; i < getColumnCount(resultSet); i++) {
            Field field = getField(entityTable.getFields(), fieldIndex);
            if (Objects.nonNull(field)) {
                mapField(resultSet, entity, field, i + 1);
                continue;
            }

            Field childField = getField(childEntityTable.getFields(), childFieldIndex);
            if (Objects.nonNull(childField)) {
                mapField(resultSet, childEntity, childField, i + 1);
            }
        }
        return childEntity;
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
