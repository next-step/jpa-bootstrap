package persistence.entity.impl.event;

import persistence.entity.ContextSource;
import persistence.entity.EventSource;

public interface EntityEvent {

    EventSource getEventSource();

    ContextSource getContextSource();

    Object getEntity();

    Object getId();

}
