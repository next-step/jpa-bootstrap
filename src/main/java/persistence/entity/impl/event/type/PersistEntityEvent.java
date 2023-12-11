package persistence.entity.impl.event.type;

import persistence.entity.ContextSource;
import persistence.entity.EventSource;
import persistence.entity.impl.EntityIdentifier;
import persistence.entity.impl.event.EntityEvent;

public class PersistEntityEvent implements EntityEvent {

    private final Class<?> clazz;
    private final EntityIdentifier entityIdentifier;
    private final Object entity;
    private final ContextSource contextSource;
    private final EventSource eventSource;

    private PersistEntityEvent(Class<?> clazz, EntityIdentifier entityIdentifier, Object entity, ContextSource contextSource, EventSource eventSource) {
        this.clazz = clazz;
        this.entityIdentifier = entityIdentifier;
        this.entity = entity;
        this.contextSource = contextSource;
        this.eventSource = eventSource;
    }

    public static PersistEntityEvent of(Object entity, EntityIdentifier entityIdentifier, ContextSource contextSource, EventSource eventSource) {
        return new PersistEntityEvent(entity.getClass(), entityIdentifier, entity, contextSource, eventSource);
    }

    public boolean isDelayedInsert() {
        return entityIdentifier.isEmptyId();
    }

    @Override
    public ContextSource getContextSource() {
        return contextSource;
    }

    @Override
    public EventSource getEventSource() {
        return eventSource;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public Object getEntity() {
        return entity;
    }

    @Override
    public Object getId() {
        throw new UnsupportedOperationException();
    }
}
