package persistence.entity.manager;

public interface EntityManager {
    <T> T find(Class<T> clazz, Object id);

    <T> void persist(T entity);

    <T> void remove(T entity);

    void flush();

    void clear();
}
