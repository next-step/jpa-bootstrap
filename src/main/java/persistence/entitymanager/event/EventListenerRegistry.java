package persistence.entitymanager.event;

import persistence.entitymanager.event.event.EventType;

public interface EventListenerRegistry {
    <T> void register(EventType<T> eventType, T listener);

    <T> EventListenerGroup<T> getEventListenerGroup(EventType<T> eventType);
}
