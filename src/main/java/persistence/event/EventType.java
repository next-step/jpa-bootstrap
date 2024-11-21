package persistence.event;

import persistence.event.delete.DeleteEventListener;
import persistence.event.load.LoadEventListener;
import persistence.event.persist.PersistEventListener;
import persistence.event.update.UpdateEventListener;

public class EventType<T> {
    public static final EventType<LoadEventListener> LOAD = create(LoadEventListener.class);
    public static final EventType<PersistEventListener> PERSIST = create(PersistEventListener.class);
    public static final EventType<DeleteEventListener> DELETE = create(DeleteEventListener.class);
    public static final EventType<UpdateEventListener> UPDATE = create(UpdateEventListener.class);

    private final Class<T> listenerInterface;

    private EventType(Class<T> listenerInterface) {
        this.listenerInterface = listenerInterface;
    }

    private static <T> EventType<T> create(Class<T> listenerInterface) {
        return new EventType<>(listenerInterface);
    }
}
