package event;

import event.load.LoadEventListener;
import event.save.SaveEventListener;

public enum EventType {
    LOAD("load", LoadEventListener.class),
    SAVE("save", SaveEventListener.class),
    ;

    private final String eventName;
    private final Class<?> eventListenerClass;

    EventType(String eventName, Class<?> eventListenerClass) {
        this.eventName = eventName;
        this.eventListenerClass = eventListenerClass;
    }
}
