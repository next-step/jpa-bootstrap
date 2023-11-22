package hibernate.event;

import hibernate.event.delete.DeleteEventListener;
import hibernate.event.load.LoadEventListener;
import hibernate.event.merge.MergeEventListener;
import hibernate.event.persist.PersistEventListener;

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
