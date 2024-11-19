package event;

public class EventType<T> {
    private final String eventName;
    private final Class<T> baseListenerInterface;

    public EventType(String eventName, Class<T> baseListenerInterface) {
        this.eventName = eventName;
        this.baseListenerInterface = baseListenerInterface;
    }

    public String getEventName() {
        return eventName;
    }

    public Class<T> getBaseListenerInterface() {
        return baseListenerInterface;
    }
}
