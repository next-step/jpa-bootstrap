package persistence.session;

import persistence.meta.Metamodel;

public interface EntityManager extends AutoCloseable {

    <T> T find(Class<T> clazz, Object id);

    void persist(Object entity);

    void remove(Object entity);

    <T> T merge(T entity);

    void flush();

    void clear();

    Metamodel getMetamodel();
}
