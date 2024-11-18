package persistence.event.persist;

import persistence.event.Event;
import persistence.event.EventListener;
import persistence.event.EventType;

public class PersistEvent<T> implements Event<T> {
    private final T entity;

    public PersistEvent(T entity) {
        this.entity = entity;
    }

    @Override
    public T getEntity() {
        return entity;
    }

    @Override
    public Class<T> getEntityType() {
        return null;
    }

    @Override
    public Object getId() {
        return null;
    }

    @Override
    public T getResult() {
        return null;
    }

    @Override
    public void setResult(T result) {

    }

    @Override
    public EventType<? extends EventListener> getEventType() {
        return EventType.PERSIST;
    }
}
