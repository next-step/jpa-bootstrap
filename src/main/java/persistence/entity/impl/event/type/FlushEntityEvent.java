package persistence.entity.impl.event.type;

import persistence.entity.ContextSource;
import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;

public class FlushEntityEvent implements EntityEvent {

    private final ContextSource contextSource;
    private final EventSource eventSource;

    private FlushEntityEvent(ContextSource contextSource, EventSource eventSource) {
        this.contextSource = contextSource;
        this.eventSource = eventSource;
    }

    public static FlushEntityEvent of(ContextSource contextSource, EventSource eventSource) {
        return new FlushEntityEvent(contextSource, eventSource);
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
        throw new RuntimeException("FlushEvent는 대상 Entity가 필요하지 않습니다.");
    }

    @Override
    public Object getId() {
        throw new RuntimeException("FlushEvent는 대상 Entity Id가 필요하지 않습니다.");
    }
}
