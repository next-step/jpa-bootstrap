package hibernate.event.persist;

import hibernate.event.EventListener;

public interface PersistEventListener extends EventListener {

    <T> void onPersist(PersistEvent<T> event);
}
