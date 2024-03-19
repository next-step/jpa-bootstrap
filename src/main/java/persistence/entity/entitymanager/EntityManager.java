package persistence.entity.entitymanager;

public interface EntityManager extends AutoCloseable {

    <T> T find(Class<T> clazz, Long id);

    <T> T persist(T entity);

    <T> void remove(T entity);

    <T> T merge(T entity);

    void close();
}
