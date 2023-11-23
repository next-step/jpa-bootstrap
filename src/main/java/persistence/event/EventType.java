package persistence.event;

public enum EventType {

    PERSIST(PersistEventListener.class),
    MERGE(MergeEventListener.class),
    DELETE(DeleteEventListener.class),
    LOAD(LoadEventListener.class);

    private final Class<? extends EventListener> listenerClass;

    EventType(final Class<? extends EventListener> listenerClass) {
        this.listenerClass = listenerClass;
    }

}
