package persistence.event;

public interface PersistEventListener {

    void onPersist(PersistEvent event);
}
