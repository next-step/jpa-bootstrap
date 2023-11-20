package persistence.event;

public class EventType<T> {
    public static final EventType<PersistEventListener> PERSIST = create("persist", PersistEventListener.class);
    public static final EventType<MergeEventListener> MERGE = create("merge", MergeEventListener.class);
    public static final EventType<DeleteEventListener> DELETE = create("delete", DeleteEventListener.class);
    public static final EventType<LoadEventListener> LOAD = create("load", LoadEventListener.class);
    private final String eventName;
    private final Class<T> baseListenerInterface;

    private EventType(final String eventName, final Class<T> baseListenerInterface) {
        this.eventName = eventName;
        this.baseListenerInterface = baseListenerInterface;
    }

    private static <T> EventType<T> create(final String name, final Class<T> listenerRole) {
        return new EventType<>(name, listenerRole);
    }
}
