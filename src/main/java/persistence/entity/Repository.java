package persistence.entity;

public interface Repository<T, ID> {

    T save(T entity);

    T saveAndFlush(T entity);

    void flush();
}
