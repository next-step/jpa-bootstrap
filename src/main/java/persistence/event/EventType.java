package persistence.event;

import persistence.event.delete.DeleteEventListener;
import persistence.event.flush.FlushEventListener;
import persistence.event.load.LoadEventListener;
import persistence.event.merge.MergeEventListener;
import persistence.event.persist.PersistEventListener;

public final class EventType<T> {

    public static final EventType<PersistEventListener> PERSIST =
            create("persist", PersistEventListener.class);
    public static final EventType<LoadEventListener> LOAD =
            create("load", LoadEventListener.class);
    public static final EventType<MergeEventListener> MERGE =
            create("merge", MergeEventListener.class);
    public static final EventType<DeleteEventListener> DELETE =
            create("delete", DeleteEventListener.class);
    public static final EventType<FlushEventListener> FLUSH =
            create("flush", FlushEventListener.class);

    private final String eventName;
    private final Class<T> listener;

    private EventType(String eventName, Class<T> listener) {
        this.eventName = eventName;
        this.listener = listener;
    }

    private static <T> EventType<T> create(String eventName, Class<T> listenerRole) {
        return new EventType<>(eventName, listenerRole);
    }
}
