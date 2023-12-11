package persistence.entity.impl.event;

import persistence.entity.ContextSource;
import persistence.entity.EventSource;

public interface EntityEventListener {

    void onEvent(EntityEvent entityEvent);

    <T> T onEvent(Class<T> clazz, EntityEvent entityEvent);

    void syncContextSource(ContextSource contextSource, Object entity);

    void syncEventSource(EventSource eventSource, Object entity);
}
