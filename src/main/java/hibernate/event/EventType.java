package hibernate.event;

import hibernate.event.listener.DeleteEventListener;
import hibernate.event.listener.LoadEventListener;
import hibernate.event.listener.MergeEventListener;
import hibernate.event.listener.PersistEventListener;

public enum EventType {
    LOAD("load", LoadEventListener.class),
    PERSIST("persist", PersistEventListener.class),
    MERGE("merge", MergeEventListener.class),
    DELETE("delete", DeleteEventListener.class),
    ;

    private final String eventName;
    private final Class<?> eventListenerClass;

    EventType(String eventName, Class<?> eventListenerClass) {
        this.eventName = eventName;
        this.eventListenerClass = eventListenerClass;
    }
}
