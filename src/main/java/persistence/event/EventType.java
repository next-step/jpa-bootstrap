package persistence.event;

public final class EventType<T> {

    public static final EventType<PersistEventListener> PERSIST =
            create("persist", PersistEventListener.class);
    public static final EventType<LoadEventListener> LOAD =
            create("load", LoadEventListener.class);
    public static final EventType<MergeEventListener> MERGE =
            create("merge", MergeEventListener.class);
    public static final EventType<DeleteEventListener> DELETE =
            create("delete", DeleteEventListener.class);

    private final String eventName;
    private final Class<T> listener;

    private EventType(String eventName, Class<T> listener) {
        this.eventName = eventName;
        this.listener = listener;
    }

    private static <T> EventType<T> create(String eventName, Class<T> listenerRole) {
        return new EventType<>(eventName, listenerRole);
    }
}
