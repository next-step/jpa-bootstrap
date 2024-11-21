package event;

public enum EventType {
    SAVE_OR_UPDATE("saveOrUpdate", SaveOrUpdateEventListener.class),
    DELETE("delete", DeleteEventListener.class),
    LOAD("load", LoadEventListener.class);

    private final String eventName;
    private final Class<?> baseListenerInterface;

    EventType(String eventName, Class<?> baseListenerInterface) {
        this.eventName = eventName;
        this.baseListenerInterface = baseListenerInterface;
    }

    public String getEventName() {
        return eventName;
    }

    public Class<?> getBaseListenerInterface() {
        return baseListenerInterface;
    }
}
