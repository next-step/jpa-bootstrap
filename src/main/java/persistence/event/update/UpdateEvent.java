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
    public EventType<? extends EventListener> getEventType() {
        return EventType.UPDATE;
    }

    public T getEntity() {
        return entity;
    }
}
