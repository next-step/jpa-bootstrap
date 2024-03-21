package database.mapping.rowmapper;

import database.dialect.Dialect;
import database.mapping.Association;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.sql.dml.part.WhereMap;
import jdbc.JdbcTemplate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.entity.database.EntityLoader2;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: 테스트 추가
public class JoinedRowsCombiner2<T> {
    private final List<JoinedRow<T>> joinedRows;
    private final Class<T> clazz;
    private final List<Association> associations;
    private final EntityLoader2<T> entityLoader2;
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private EntityMetadata entityMetadata;

    public JoinedRowsCombiner2(List<JoinedRow<T>> joinedRows,
                               Class<T> clazz,
                               List<Association> associations,
                               EntityLoader2<T> entityLoader2) {
        this.joinedRows = joinedRows;
        this.clazz = clazz;
        this.associations = associations;
        this.entityLoader2 = entityLoader2;
        jdbcTemplate = null;
        dialect = null;
    }

    public JoinedRowsCombiner2(List<JoinedRow<T>> joinedRows,
                               Class<T> clazz,
                               List<Association> associations,
                               EntityLoader2<T> entityLoader,
                               JdbcTemplate jdbcTemplate,
                               Dialect dialect) {
        entityMetadata = EntityMetadataFactory.get(clazz);
        this.joinedRows = joinedRows;
        this.clazz = clazz;
        this.associations = associations;
        this.entityLoader2 = entityLoader;
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
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
                Long id = EntityMetadataFactory.get(clazz).getPrimaryKeyValue(entity);
                value = lazyLoadProxy(association, id);
                setFieldValue(entity, fieldName, value);
            } else {
                // TODO: 연관관계가 Collection 이 아니면 달라져야 함
                value = filterEntitiesByType(association.getFieldGenericType());
            }
            setFieldValue(entity, fieldName, value);
        }

        return Optional.of(entity);
    }

    private <R> Object lazyLoadProxy(Association association, Long id) {
        return Enhancer.create(association.getFieldType(), (LazyLoader) () -> {
            Class<?> genericType = association.getFieldGenericType();
            WhereMap whereMap = WhereMap.of(association.getForeignKeyColumnName(), id);
            EntityLoader2<R> entityLoader1 = new EntityLoader2<>(((Class<R>) genericType), jdbcTemplate, dialect);
            return entityLoader1.load(whereMap);
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
        return entityMetadata.getFieldByFieldName(fieldName);
    }

    private <R> List<R> filterEntitiesByType(Class<R> associatedType) {
        return joinedRows.stream().map(joinedRow -> joinedRow.mapValues(associatedType)).collect(Collectors.toList());
    }
}
