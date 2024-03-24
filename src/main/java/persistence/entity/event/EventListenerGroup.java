package persistence.entity.event;

public class EventListenerGroup <T extends EventListener > {

    private final EventType eventType;
    private final T listener;

    public EventListenerGroup(EventType eventType, T listener) {
        this.eventType = eventType;
        this.listener = listener;
    }

    public T getListener() {
        return listener;
    }

}
