package event;

public interface EventListenerRegistry {
    void addEventListenerGroup(EventType<? extends EventListener> eventType, EventListenerGroup<?> listenerGroup);
    EventListenerGroup<?> getEventListenerGroup(EventType<? extends EventListener> eventType);
}
