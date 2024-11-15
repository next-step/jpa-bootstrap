package persistence.event;

import java.util.HashMap;
import java.util.Map;

public class EventListenerRegistry {
    private final Map<EventType<?>, EventListenerGroup<?>> eventListenerGroupRegistry = new HashMap<>();

    public EventListenerRegistry() {
        registerLoadEventListener();
        registerPersistEventListener();
    }

    public <T> EventListenerGroup<T> getEventListenerGroup(EventType<T> eventType) {
        return (EventListenerGroup<T>) eventListenerGroupRegistry.get(eventType);
    }

    private void registerLoadEventListener() {
        final EventListenerGroup<LoadEventListener> eventListenerGroup = new EventListenerGroup<>(EventType.LOAD);
        eventListenerGroup.appendListener(new CacheLoadEventListener());
        eventListenerGroup.appendListener(new DefaultLoadEventListener());
        eventListenerGroupRegistry.put(EventType.LOAD, eventListenerGroup);
    }

    private void registerPersistEventListener() {
        final EventListenerGroup<PersistEventListener> eventListenerGroup = new EventListenerGroup<>(EventType.PERSIST);
        eventListenerGroup.appendListener(new DefaultPersistEventListener());
        eventListenerGroupRegistry.put(EventType.PERSIST, eventListenerGroup);
    }
}
