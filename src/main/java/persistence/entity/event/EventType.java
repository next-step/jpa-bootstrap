package persistence.entity.event;

import persistence.entity.event.load.LoadEventListener;

public enum EventType {

    LOAD("load", LoadEventListener.class),
    SAVE("save", PersistEventListener.class),
    UPDATE("update", PersistEventListener.class),
    DELETE("delete", PersistEventListener.class),
    ;

    private final String eventName;
    private final Class<?> baseListenerInterface;

    private EventType(String eventName, Class<?> baseListenerInterface) {
        this.eventName = eventName;
        this.baseListenerInterface = baseListenerInterface;
    }

}
