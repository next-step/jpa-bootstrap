package persistence.event;

public interface PersistEventListener {
    void onPersist(final PersistEvent persistEvent);
}
