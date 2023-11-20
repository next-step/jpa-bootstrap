package persistence.event;

public interface PersistEventListener extends EventListener {
    void onPersist(final PersistEvent persistEvent);
}
