package persistence.event.persist;

public interface PersistEventListener {

    void onPersist(PersistEvent event);
}
