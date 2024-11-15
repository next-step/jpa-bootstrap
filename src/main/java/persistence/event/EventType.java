package persistence.event;

import persistence.event.delete.DeleteEventListener;
import persistence.event.dirtycheck.DirtyCheckEventListener;
import persistence.event.load.LoadEventListener;
import persistence.event.persist.PersistEventListener;
import persistence.event.update.UpdateEventListener;

public class EventType<T> {
    public static final EventType<LoadEventListener> LOAD = create("load", LoadEventListener.class);
    public static final EventType<PersistEventListener> PERSIST = create("create", PersistEventListener.class);
    public static final EventType<PersistEventListener> PERSIST_ONFLUSH = create("create-onflush", PersistEventListener.class);
    public static final EventType<DeleteEventListener> DELETE = create("delete", DeleteEventListener.class);
    public static final EventType<UpdateEventListener> UPDATE = create("update", UpdateEventListener.class);
    public static final EventType<DirtyCheckEventListener> DIRTY_CHECK = create("dirty-check", DirtyCheckEventListener.class);

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
