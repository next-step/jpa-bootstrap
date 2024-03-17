package persistence.entity;

public interface EntityPersister<T> {

    boolean update(Object entity);

    Object insert(Object entity);

    void delete(Object entity);
}
