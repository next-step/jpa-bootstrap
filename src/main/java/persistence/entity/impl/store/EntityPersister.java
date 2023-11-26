package persistence.entity.impl.store;

public interface EntityPersister {

    void update(Object entity);

    Object store(Object entity);

    void delete(Object entity);
}
