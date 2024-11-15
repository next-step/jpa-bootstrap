package event;

public class EventListenerGroup<T> {

    private final T eventListener;

    public EventListenerGroup(T eventListener) {
        this.eventListener = eventListener;
    }

    public T getEventListener() {
        return eventListener;
    }
}
