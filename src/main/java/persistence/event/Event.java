package persistence.event;

public interface Event<T> {
    Class<T> getEntityType();

    T getEntity();

    Object getId();

    T getResult();

    void setResult(T result);

    EventType<? extends EventListener> getEventType();
}
