package persistence.event;

public interface Event<T> {
    EventType<? extends EventListener> getEventType();

    default Class<T> getEntityType() {
        throw new UnsupportedOperationException();
    }

    default T getEntity() {
        throw new UnsupportedOperationException();
    }

    default Object getId() {
        throw new UnsupportedOperationException();
    }

    default T getResult() {
        throw new UnsupportedOperationException();
    }

    default void setResult(T result) {
        throw new UnsupportedOperationException();
    }
}
