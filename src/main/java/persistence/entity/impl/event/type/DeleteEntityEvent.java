package persistence.entity.impl.event.type;

import persistence.entity.ContextSource;
import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;

public class DeleteEntityEvent implements EntityEvent {

    private final Object entity;
    private final ContextSource contextSource;
    private final EventSource eventSource;

    private DeleteEntityEvent(Object entity, ContextSource contextSource, EventSource eventSource) {
        this.entity = entity;
        this.contextSource = contextSource;
        this.eventSource = eventSource;
    }

    public static DeleteEntityEvent of(Object entity, ContextSource contextSource, EventSource eventSource) {
        return new DeleteEntityEvent(entity, contextSource, eventSource);
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
}
