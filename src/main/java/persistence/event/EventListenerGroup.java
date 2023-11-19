package persistence.event;


import persistence.entity.persister.EntityPersisters;

public class EventListenerGroup {

    private final PersistEventListener persistEventListener;

    public EventListenerGroup(final EntityPersisters entityPersisters) {
        this.persistEventListener = new DefaultPersisEventListener(entityPersisters);
    }

    public void persist(final PersistEvent persistEvent) {
        persistEventListener.onPersist(persistEvent);
    }
}
