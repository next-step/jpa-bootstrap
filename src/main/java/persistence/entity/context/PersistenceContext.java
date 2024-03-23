package persistence.entity.context;

public interface PersistenceContext {
    <T> Object getEntity(PersistentClass<T> persistentClass, Long id);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    boolean isRemoved(Object entity);
}
