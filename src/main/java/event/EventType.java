package event;

public class EventType<T> {
    public static final EventType<SaveOrUpdateEventListener> SAVE_OR_UPDATE = create("saveOrUpdate", SaveOrUpdateEventListener.class);
    public static final EventType<DeleteEventListener> DELETE = create("delete", DeleteEventListener.class);
    public static final EventType<LoadEventListener> LOAD = create("load", LoadEventListener.class);

    private final String eventName;
    private final Class<T> baseListenerInterface;

    public EventType(String eventName, Class<T> baseListenerInterface) {
        this.eventName = eventName;
        this.baseListenerInterface = baseListenerInterface;
    }

    private static <T> EventType<T> create(String eventName, Class<T> baseListenerInterface) {
        return new EventType<>(eventName, baseListenerInterface);
    }

    public String getEventName() {
        return eventName;
    }

    public Class<T> getBaseListenerInterface() {
        return baseListenerInterface;
    }
}
