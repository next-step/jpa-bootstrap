package persistence.entity.context;

public interface PersistenceContext {
    <T> T getEntity(PersistentClass<T> persistentClass, Long id);

    <T> void addEntity(T entity);

    void removeEntity(Object entity);
}
