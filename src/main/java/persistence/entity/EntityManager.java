package persistence.entity;

import jakarta.persistence.FlushModeType;
import persistence.sql.schema.meta.EntityClassMappingMeta;

public interface EntityManager extends AutoCloseable, EventSource {

    <T> T find(Class<T> clazz, Object Id);

    Object persist(Object entity);

    void remove(Object entity);

    void clear();

    <T> T merge(Class<T> clazz, T t);

    void flush();

    EntityClassMappingMeta getEntityMeta(Class<?> clazz);

    FlushModeType getFlushModeType();

    void setAutoCommit();

    void setManualCommit();
}
