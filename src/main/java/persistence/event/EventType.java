package persistence.event;

public class EventType<T> {
    public static final EventType<LoadEventListener> LOAD = create("load", LoadEventListener.class);

    private final String name;
    private final Class<T> listenerInterface;

    private EventType(String name, Class<T> listenerInterface) {
        this.name = name;
        this.listenerInterface = listenerInterface;
    }

    private static <T> EventType<T> create(String name, Class<T> listenerInterface) {
        return new EventType<>(name, listenerInterface);
    }
}
