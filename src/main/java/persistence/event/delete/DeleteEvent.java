package persistence.event.delete;

import persistence.event.Event;
import persistence.event.EventListener;
import persistence.event.EventType;

public class DeleteEvent<T> implements Event<T> {
    private final T entity;

    public DeleteEvent(T entity) {
        this.entity = entity;
    }

    @Override
    public EventType<? extends EventListener> getEventType() {
        return EventType.DELETE;
    }

    public T getEntity() {
        return entity;
    }
}
