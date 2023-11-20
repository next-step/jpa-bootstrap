package persistence.event;


import persistence.entity.persister.EntityPersisters;

public class EventListenerGroup {

    private final PersistEventListener persistEventListener;
    private final MergeEventListener mergeEventListener;

    public EventListenerGroup(final EntityPersisters entityPersisters) {
        this.persistEventListener = new DefaultPersisEventListener(entityPersisters);
        this.mergeEventListener = new DefaultMergeEventListener(entityPersisters);
    }

    public void persist(final PersistEvent persistEvent) {
        persistEventListener.onPersist(persistEvent);
    }
    public void merge(final MergeEvent mergeEvent) {
        mergeEventListener.onMerge(mergeEvent);
    }
}
