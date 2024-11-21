package persistence.event.load;

import persistence.event.Event;
import persistence.event.EventListener;
import persistence.event.EventType;

public class LoadEvent<T> implements Event<T> {
    private final Class<T> entityType;
    private final Object id;
    private T result;

    public LoadEvent(Class<T> entityType, Object id) {
        this.entityType = entityType;
        this.id = id;
    }

    @Override
    public EventType<? extends EventListener> getEventType() {
        return EventType.LOAD;
    }

    @Override
    public Class<T> getEntityType() {
        return entityType;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public T getResult() {
        return result;
    }

    @Override
    public void setResult(T result) {
        this.result = result;
    }
}
