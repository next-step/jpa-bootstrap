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
    @Override
    public <T> boolean supports(Class<T> entityType) {
        final EntityTable entityTable = new EntityTable(entityType);
        return !entityTable.isSimpleMapping();
    }

    @Override
    public <T> T getRow(ResultSet resultSet, Class<T> entityType) throws IllegalAccessException, SQLException {
        final List<Field> fields = getPersistentFields(entityType);
        final Class<?> associationColumnType = getAssociationColumnType(entityType);

        final T entity = new InstanceFactory<>(entityType).createInstance();
        final List<Object> collection = getCollection(fields, entity);

        do {
            collection.add(getChildEntity(resultSet, associationColumnType, fields, entity));
        } while (resultSet.next());

        return entity;
    }

    private List<Object> getCollection(List<Field> fields, Object entity) throws IllegalAccessException {
        final Field collectionField = findListField(fields);
        collectionField.setAccessible(true);

        Object collection = collectionField.get(entity);
        if (collection instanceof List<?>) {
            final List<Object> newCollection = new ArrayList<>((List<?>) collection);
            collectionField.set(entity, newCollection);
            return newCollection;
        }
        throw new ClassCastException();
    }

    private Object getChildEntity(ResultSet resultSet, Class<?> associationColumnType, List<Field> fields, Object entity) throws SQLException, IllegalAccessException {
        final Object childEntity = new InstanceFactory<>(associationColumnType).createInstance();
        final AtomicInteger fieldIndex = new AtomicInteger(0);
        final AtomicInteger childFieldIndex = new AtomicInteger(0);

        for (int i = 0; i < getColumnCount(resultSet); i++) {
            Field field = getField(fields, fieldIndex);
            if (Objects.nonNull(field)) {
                mapField(resultSet, entity, field, i + 1);
                continue;
            }

            Field childField = getField(getPersistentFields(associationColumnType), childFieldIndex);
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

    private Field findListField(List<Field> fields) {
        return fields.stream()
                .filter(field -> field.getType() == List.class)
                .findFirst()
                .orElseThrow();
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
