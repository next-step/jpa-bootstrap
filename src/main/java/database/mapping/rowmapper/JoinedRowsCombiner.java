package database.mapping.rowmapper;

import database.dialect.Dialect;
import database.mapping.Association;
import database.sql.dml.part.WhereMap;
import jdbc.JdbcTemplate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.entity.context.PersistentClass;
import persistence.entity.database.EntityLoader;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: 테스트 추가
public class JoinedRowsCombiner<T> {
    private final List<JoinedRow<T>> joinedRows;
    private final PersistentClass<T> persistentClass;
    private final List<Association> associations;
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private final List<Class<?>> entities;

    public JoinedRowsCombiner(List<JoinedRow<T>> joinedRows,
                              PersistentClass<T> persistentClass,
                              List<Association> associations,
                              JdbcTemplate jdbcTemplate,
                              Dialect dialect,
                              List<Class<?>> entities) {
        this.joinedRows = joinedRows;
        this.persistentClass = persistentClass;
        this.associations = associations;
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
        this.entities = entities;
    }

    public Optional<T> merge() {
        if (joinedRows.isEmpty()) {
            return Optional.empty();
        }

        T entity = joinedRows.get(0).getOwnerEntity();

        for (Association association : this.associations) {
            String fieldName = association.getFieldName();

            Object value;
            if (association.isLazyLoad()) {
                Long id = persistentClass.getPrimaryKeyValue(entity);
                value = lazyLoadProxy(association, id);
                setFieldValue(entity, fieldName, value);
            } else {
                // TODO: 연관관계가 Collection 이 아니면 달라져야 함
                Class<?> genericType = association.getFieldGenericType();
                value = filterEntitiesByType(genericType);
            }
            setFieldValue(entity, fieldName, value);
        }

        return Optional.of(entity);
    }

    private <R> Object lazyLoadProxy(Association association, Long id) {
        return Enhancer.create(association.getFieldType(), (LazyLoader) () -> {
            Class<R> genericType = (Class<R>) association.getFieldGenericType();

            // TODO: entityloader 생성을 다른곳에 맡기면 좋겠는데? entities 등의 세션정보를 받아올 필요가 있다.
            PersistentClass<R> persistentClass = PersistentClass.from(genericType);
            EntityLoader<R> entityLoaderForProxy = new EntityLoader<>(persistentClass, jdbcTemplate, dialect, entities);

            WhereMap whereMap = WhereMap.of(association.getForeignKeyColumnName(), id);
            return entityLoaderForProxy.load(whereMap);
        });
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

    private <R> List<R> filterEntitiesByType(Class<R> associatedType) {
        PersistentClass<R> persistentClass1 = PersistentClass.from(associatedType);
        return joinedRows.stream()
                .map(joinedRow -> joinedRow.mapValues(persistentClass1))
                .collect(Collectors.toList());
    }
}
