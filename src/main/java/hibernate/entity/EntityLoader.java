package hibernate.entity;

import hibernate.dml.SelectQueryBuilder;
import hibernate.entity.collection.PersistentList;
import hibernate.entity.meta.EntityClass;
import hibernate.entity.meta.column.EntityJoinColumn;
import hibernate.entity.meta.column.EntityJoinColumns;
import jdbc.JdbcTemplate;
import jdbc.ReflectionRowMapper;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;

import java.util.List;

public class EntityLoader<T> {

    private final JdbcTemplate jdbcTemplate;
    private final EntityClass<T> entityClass;

    private final SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.INSTANCE;

    public EntityLoader(final JdbcTemplate jdbcTemplate, final EntityClass<T> entityClass) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityClass = entityClass;
    }

    public T find(final Object id) {
        EntityJoinColumns entityJoinColumns = EntityJoinColumns.oneToManyColumns(entityClass);
        T instance = getInstance(id, entityJoinColumns);

        if (entityJoinColumns.hasLazyFetchType()) {
            setLazyJoinColumns(entityJoinColumns.getLazyValues(), instance);
        }
        return instance;
    }

    public List<T> findAll() {
        final String query = selectQueryBuilder.generateAllQuery(entityClass.tableName(), entityClass.getFieldNames());
        return jdbcTemplate.query(query, ReflectionRowMapper.getInstance(entityClass));
    }

    private T getInstance(Object id, EntityJoinColumns entityJoinColumns) {
        if (entityJoinColumns.hasEagerFetchType()) {
            return queryWithEagerColumn(id, entityJoinColumns);
        }
        return queryOnlyEntity(id);
    }

    private T queryWithEagerColumn(Object id, EntityJoinColumns entityJoinColumns) {
        final String query = selectQueryBuilder.generateQuery(
                entityClass.tableName(),
                entityClass.getFieldNames(),
                entityClass.getEntityId(),
                id,
                entityJoinColumns.getEagerJoinTableFields(),
                entityJoinColumns.getEagerJoinTableIds()
        );
        return jdbcTemplate.queryForObject(query, ReflectionRowMapper.getInstance(entityClass));
    }

    private T queryOnlyEntity(Object id) {
        final String query = selectQueryBuilder.generateQuery(
                entityClass.tableName(),
                entityClass.getFieldNames(),
                entityClass.getEntityId(),
                id
        );
        return jdbcTemplate.queryForObject(query, ReflectionRowMapper.getInstance(entityClass));
    }

    private void setLazyJoinColumns(List<EntityJoinColumn> lazyJoinColumns, T instance) {
        for (EntityJoinColumn lazyJoinColumn : lazyJoinColumns) {
            Enhancer enhancer = generateEnhancer(lazyJoinColumn.getEntityClass());
            lazyJoinColumn.assignFieldValue(instance, enhancer.create());
        }
    }

    private <K> Enhancer generateEnhancer(EntityClass<K> entityClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(List.class);
        enhancer.setCallback((LazyLoader) () -> new PersistentList<>(entityClass, new EntityLoader<>(jdbcTemplate, entityClass)));
        return enhancer;
    }
}
