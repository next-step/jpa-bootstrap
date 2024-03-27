package persistence.entitymanager.event;

import persistence.entitymanager.event.event.EventType;

import java.util.HashMap;
import java.util.Map;

public class EventListenerRegistryImpl implements EventListenerRegistry {
    private final Map<EventType<?>, Object> listeners;

    private final Map<EventType<?>, Object> listenerGroupMap;

    public EventListenerRegistryImpl() {
        this.listeners = new HashMap<>();
        this.listenerGroupMap = new HashMap<>();
    }

    @Override
    public <T> void register(EventType<T> eventType, T listener) {
        listeners.put(eventType, listener);

        if (!listenerGroupMap.containsKey(eventType)) {
            listenerGroupMap.put(eventType, new EventListenerGroup<>());
        }
        getListenerGroup(eventType).registerListener(listener);
    }

    private <T> EventListenerGroup<T> getListenerGroup(EventType<T> eventType) {
        return ((EventListenerGroup<T>) listenerGroupMap.get(eventType));
    }

    @Override
    public <T> EventListenerGroup<T> getEventListenerGroup(EventType<T> eventType) {
        T listener = (T) listeners.get(eventType);
        EventListenerGroup<T> tEventListenerGroup = new EventListenerGroup<>();
        tEventListenerGroup.registerListener(listener);
        return tEventListenerGroup;
    }
}
