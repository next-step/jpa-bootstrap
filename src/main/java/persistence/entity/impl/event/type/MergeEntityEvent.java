package persistence.entity.impl.event.type;

import persistence.entity.ContextSource;
import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;

public class MergeEntityEvent implements EntityEvent {

    private final Class<?> clazz;
    private final Object entity;
    private final ContextSource contextSource;
    private final EventSource eventSource;


    private MergeEntityEvent(Class<?> clazz, Object entity, ContextSource contextSource, EventSource eventSource) {
        this.clazz = clazz;
        this.entity = entity;
        this.contextSource = contextSource;
        this.eventSource = eventSource;
    }

    public static MergeEntityEvent of(Object entity, ContextSource contextSource, EventSource eventSource) {
        return new MergeEntityEvent(entity.getClass(), entity, contextSource, eventSource);
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
        return entity;
    }

    @Override
    public Object getId() {
        throw new UnsupportedOperationException();
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
