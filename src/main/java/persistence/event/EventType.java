package persistence.event;

public final class EventType<T> {

    public static final EventType<PersistEventListener> PERSIST =
            create("persist", PersistEventListener.class);

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
