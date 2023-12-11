package persistence.entity.impl.event.type;

import persistence.entity.ContextSource;
import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;

public class LoadEntityEvent implements EntityEvent {

    private final Object id;
    private final Class<?> clazz;
    private final ContextSource contextSource;
    private final EventSource eventSource;

    private LoadEntityEvent(ContextSource contextSource, Class<?> clazz, Object id, EventSource eventSource) {
        this.clazz = clazz;
        this.contextSource = contextSource;
        this.id = id;
        this.eventSource = eventSource;
    }

    public static LoadEntityEvent of(Class<?> clazz, Object id, ContextSource contextSource, EventSource eventSource) {
        return new LoadEntityEvent(contextSource, clazz, id, eventSource);
    }

    @Override
    public ContextSource getContextSource() {
        return contextSource;
    }

    @Override
    public EventSource getEventSource() {
        return eventSource;
    }

    @Override
    public Object getEntity() {
        return null;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getId() {
        return id;
    }
}
