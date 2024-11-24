package event;

public interface Event<T> {
    T entity();

    String entityName();

    Object entityId();
}
