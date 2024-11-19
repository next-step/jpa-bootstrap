package event;

import event.listener.delete.DeleteEventListener;
import event.listener.load.LoadEventListener;
import event.listener.merge.MergeEventListener;
import event.listener.persist.PersistEventListener;

public enum EventType {

    LOAD(LoadEventListener.class),
    PERSIST(PersistEventListener.class),
    MERGE(MergeEventListener.class),
    DELETE(DeleteEventListener.class);

    private final Class<?> clazz;

    EventType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
