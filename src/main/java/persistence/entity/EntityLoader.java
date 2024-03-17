package persistence.entity;

public interface EntityLoader<T> {

    T find(Object Id);
}
