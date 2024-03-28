package persistence.entitymanager.listener;

import persistence.entitymanager.listener.events.EventType;

public interface EventListenerRegistry {
    <T> void register(EventType<T> eventType, T listener);

    <T> EventListenerGroup<T> getEventListenerGroup(EventType<T> eventType);
}
