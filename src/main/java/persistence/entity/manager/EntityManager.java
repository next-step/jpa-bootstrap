package persistence.entity.manager;

public interface EntityManager {

    <T> T find(Class<T> clazz, Object Id);

    void persist(Object entity);

    <T> T merge(T entity);

    void remove(Object entity);

    void flush();

    void close();
}
