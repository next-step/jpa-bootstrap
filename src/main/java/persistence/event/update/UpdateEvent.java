package persistence.event.update;

import persistence.event.Event;
import persistence.event.EventListener;
import persistence.event.EventType;

public class UpdateEvent<T> implements Event<T> {
    private final T entity;

    public UpdateEvent(T entity) {
        this.entity = entity;
    }

    @Override
    public Class<T> getEntityType() {
        return null;
    }

    @Override
    public T getEntity() {
        return entity;
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
        return EventType.UPDATE;
    }
}
