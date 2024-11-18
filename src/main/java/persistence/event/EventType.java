package persistence.event;

import persistence.event.clear.ClearEventListener;
import persistence.event.delete.DeleteEventListener;
import persistence.event.dirtycheck.DirtyCheckEventListener;
import persistence.event.flush.FlushEventListener;
import persistence.event.load.LoadEventListener;
import persistence.event.merge.MergeEventListener;
import persistence.event.persist.PersistEventListener;
import persistence.event.update.UpdateEventListener;

public class EventType<T> {
    public static final EventType<LoadEventListener> LOAD = create("load", LoadEventListener.class);
    public static final EventType<PersistEventListener> PERSIST = create("create", PersistEventListener.class);
    public static final EventType<DeleteEventListener> DELETE = create("delete", DeleteEventListener.class);
    public static final EventType<UpdateEventListener> UPDATE = create("update", UpdateEventListener.class);
    public static final EventType<DirtyCheckEventListener> DIRTY_CHECK = create("dirty-check", DirtyCheckEventListener.class);
    public static final EventType<MergeEventListener> MERGE = create("merge", MergeEventListener.class);
    public static final EventType<FlushEventListener> FLUSH = create("flush", FlushEventListener.class);
    public static final EventType<ClearEventListener> CLEAR = create("clear", ClearEventListener.class);

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
