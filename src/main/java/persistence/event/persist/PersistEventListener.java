package persistence.event.persist;

import persistence.event.EventListener;

public interface PersistEventListener extends EventListener {
    <T> void onPersist(PersistEvent<T> persistEvent);
}
