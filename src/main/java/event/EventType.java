package event;

public enum EventType {
    LOAD("load", LoadEventListener.class),
    ;

    private final String eventName;
    private final Class<?> eventListenerClass;

    EventType(String eventName, Class<?> eventListenerClass) {
        this.eventName = eventName;
        this.eventListenerClass = eventListenerClass;
    }
}
