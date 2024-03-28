package persistence.entitymanager.event;

import persistence.bootstrap.Metadata;
import persistence.entitymanager.event.event.EventType;
import persistence.entitymanager.event.listeners.DeleteEventListener;
import persistence.entitymanager.event.listeners.LoadEventListener;
import persistence.entitymanager.event.listeners.PersistEventListener;

import java.util.HashMap;
import java.util.Map;

public class EventListenerRegistryImpl implements EventListenerRegistry {
    private final Map<EventType<?>, Object> listeners;

    private final Map<EventType<?>, Object> listenerGroupMap;

    public static EventListenerRegistry buildEventListenerRegistry(Metadata metadata) {
        EventListenerRegistry registry = new EventListenerRegistryImpl();
        registry.register(EventType.LOAD, new LoadEventListener(metadata));
        registry.register(EventType.PERSIST, new PersistEventListener());
        registry.register(EventType.DELETE, new DeleteEventListener());
        return registry;
    }

    private EventListenerRegistryImpl() {
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
