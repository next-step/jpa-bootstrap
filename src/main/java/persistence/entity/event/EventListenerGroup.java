package persistence.entity.event;


import java.util.Map;

public class EventListenerGroup <T extends EventListener > {

    private final Map<EventType, T> listeners;

    public EventListenerGroup(Map<EventType, T> listeners) {
        this.listeners = listeners;
    }

    public T getListener(EventType eventType) {
        return listeners.get(eventType);
    }

}
