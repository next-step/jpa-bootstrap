package persistence.entity.persister;

public interface EntityPersister<T> {

    boolean update(T entity);
    void insert(T entity);
    void delete(T entity);
}
