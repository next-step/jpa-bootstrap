package database.mapping.rowmapper;

import database.dialect.Dialect;
import database.mapping.Association;
import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.sql.dml.part.WhereMap;
import jdbc.JdbcTemplate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.entity.database.EntityLoader;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: 테스트 추가
public class JoinedRowsCombiner<T> {
    private final List<JoinedRow<T>> joinedRows;
    private final List<Association> associations;
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private final EntityMetadata entityMetadata;

    public JoinedRowsCombiner(List<JoinedRow<T>> joinedRows,
                              Class<T> clazz,
                              List<Association> associations,
                              JdbcTemplate jdbcTemplate,
                              Dialect dialect) {
        // XXX 이거 딴데서 가져올수 있나?
        this.entityMetadata = EntityMetadataFactory.get(clazz);
        this.joinedRows = joinedRows;
        this.associations = associations;
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
                Long id = entityMetadata.getPrimaryKeyValue(entity);
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
            Class<R> genericType = (Class<R>) association.getFieldGenericType();
            EntityLoader<R> entityLoaderForProxy = new EntityLoader<>(genericType, jdbcTemplate, dialect);

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
        return entityMetadata.getFieldByFieldName(fieldName);
    }

    private <R> List<R> filterEntitiesByType(Class<R> associatedType) {
        return joinedRows.stream().map(joinedRow -> joinedRow.mapValues(associatedType)).collect(Collectors.toList());
    }
}
