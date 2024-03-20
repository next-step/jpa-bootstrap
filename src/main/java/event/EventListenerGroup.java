package event;


import boot.metamodel.MetaModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventListenerGroup {

    private final Map<EventType, EventListener<?>> eventListeners;

    private EventListenerGroup(Map<EventType, EventListener<?>> eventListeners) {
        this.eventListeners = eventListeners;
    }

    public static EventListenerGroup createDefaultGroup(MetaModel metaModel) {
        Map<EventType, EventListener<?>> eventListeners = new ConcurrentHashMap<>();
        DefaultLoadEventListener defaultLoadEventListener = new DefaultLoadEventListener(metaModel);
        eventListeners.put(EventType.LOAD, defaultLoadEventListener);
        return new EventListenerGroup(eventListeners);
    }

    @SuppressWarnings("unchecked")
    public <T> EventListener<T> getListener(EventType eventType) {
        if (!eventListeners.containsKey(eventType)) {
            throw new IllegalArgumentException("EventListener does not exist for the type : " + eventType);
        }
        return (EventListener<T>) eventListeners.get(eventType);
    }
}
