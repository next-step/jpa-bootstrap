package event;

public interface EventListenerGroup<T> {

    void addEventListener(EventListener listener);

    void fireEvent(Event event);

    EventType<T> getEventType();
}
