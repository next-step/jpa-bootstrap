package persistence.entity;

import persistence.sql.schema.meta.EntityClassMappingMeta;

public interface EntityManager extends AutoCloseable {

    <T> T find(Class<T> clazz, Object Id);

    Object persist(Object entity);

    void remove(Object entity);

    void clear();

    <T> T merge(Class<T> clazz, T t);

    EntityClassMappingMeta getEntityMeta(Class<?> clazz);
}
