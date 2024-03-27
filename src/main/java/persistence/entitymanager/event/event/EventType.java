package persistence.entitymanager.event.event;

import persistence.entitymanager.event.listeners.DeleteEventListener;
import persistence.entitymanager.event.listeners.LoadEventListener;
import persistence.entitymanager.event.listeners.PersistEventListener;

public class EventType<T> {
    public static final EventType<LoadEventListener> LOAD = create("load", LoadEventListener.class);
    public static final EventType<PersistEventListener> PERSIST = create("persist", PersistEventListener.class);
    public static final EventType<DeleteEventListener> DELETE = create("delete", DeleteEventListener.class);

    private final String eventName;
    private final Class<T> baseListenerInterface;

    private EventType(String eventName, Class<T> baseListenerInterface) {
        this.eventName = eventName;
        this.baseListenerInterface = baseListenerInterface;
    }

    private static <T> EventType<T> create(String name, Class<T> listenerRole) {
        return new EventType<>(name, listenerRole);
    }
}
