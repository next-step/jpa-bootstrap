package persistence.event;

public interface Event<T> {
    EventType<? extends EventListener> getEventType();
}
