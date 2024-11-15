package persistence.entity.manager;

public interface EntityManager {
    <T> T find(Class<T> clazz, Object id);

    <T> void persist(T entity);

    void remove(Object entity);

    void flush();

    void clear();
}
