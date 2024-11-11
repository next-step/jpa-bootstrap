package persistence.event;

public interface PersistEventListener extends BaseEventListener {

    void onPersist(PersistEvent event);
}
