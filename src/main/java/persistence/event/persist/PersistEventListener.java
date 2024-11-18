package persistence.event.persist;

public interface PersistEventListener {
    <T> void onPersist(PersistEvent<T> persistEvent);
}
