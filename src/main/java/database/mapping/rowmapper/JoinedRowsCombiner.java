package database.mapping.rowmapper;

import database.mapping.Association;
import database.sql.dml.part.WhereMap;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.bootstrap.Metadata;
import persistence.bootstrap.Metamodel;
import persistence.entity.context.PersistentClass;
import persistence.entity.database.EntityLoader;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: 테스트 추가
public class JoinedRowsCombiner<T> {
    private final PersistentClass<T> persistentClass;
    private final Metadata metadata;
    private final Metamodel metamodel;

    public JoinedRowsCombiner(PersistentClass<T> persistentClass, Metadata metadata, Metamodel metamodel) {
        this.persistentClass = persistentClass;
        this.metadata = metadata;
        this.metamodel = metamodel;
    }

    public Optional<T> merge(List<JoinedRow<T>> joinedRows) {
        if (joinedRows.isEmpty()) {
            return Optional.empty();
        }

        T entity = joinedRows.get(0).getOwnerEntity();

        for (Association association : this.persistentClass.getAssociations()) {
            String fieldName = association.getFieldName();
            Object value = getFieldValue(association, entity, joinedRows);
            setFieldValue(entity, fieldName, value);
        }

        return Optional.of(entity);
    }

    private Object getFieldValue(Association association, T entity, List<JoinedRow<T>> joinedRows) {
        if (association.isLazyLoad()) {
            return buildLazyLoadProxy(association, metadata.getRowId(entity));
        }
        return filterEntitiesByType(association.getGenericTypeClass(metadata), joinedRows);
    }

    private <R> Object buildLazyLoadProxy(Association association, Long id) {
        Class<?> fieldType = association.getFieldType();
        return Enhancer.create(fieldType, (LazyLoader) () -> {
            Class<?> genericType = association.getFieldGenericType();
            EntityLoader<R> entityLoaderForProxy = metamodel.getEntityLoader((Class<R>) genericType);
            return entityLoaderForProxy.load(WhereMap.of(association.getForeignKeyColumnName(), id));
        });
    }

    private <R> List<R> filterEntitiesByType(PersistentClass<R> persistentClass, List<JoinedRow<T>> joinedRows) {
        return joinedRows.stream()
                .map(joinedRow -> joinedRow.mapValues(persistentClass))
                .collect(Collectors.toList());
    }

    private <R> void setFieldValue(R entity, String fieldName, Object value) {
        Field field = getFieldByName(fieldName);
        try {
            field.setAccessible(true);
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Field getFieldByName(String fieldName) {
        return persistentClass.getFieldByFieldName(fieldName);
    }
}
