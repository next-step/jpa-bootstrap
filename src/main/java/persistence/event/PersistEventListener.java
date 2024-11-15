package persistence.event;

public interface PersistEventListener {
    <T> void onPersist(PersistEvent<T> persistEvent);
}
