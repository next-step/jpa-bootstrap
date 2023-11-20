package persistence.event;


import persistence.entity.persister.EntityPersisters;

public class EventListenerGroup {

    private final PersistEventListener persistEventListener;
    private final MergeEventListener mergeEventListener;
    private final DeleteEventListener deleteEventListener;

    public EventListenerGroup(final EntityPersisters entityPersisters) {
        this.persistEventListener = new DefaultPersisEventListener(entityPersisters);
        this.mergeEventListener = new DefaultMergeEventListener(entityPersisters);
        this.deleteEventListener = new DefaultDeleteEventListener(entityPersisters);
    }

    public void persist(final PersistEvent persistEvent) {
        persistEventListener.onPersist(persistEvent);
    }
    public void merge(final MergeEvent mergeEvent) {
        mergeEventListener.onMerge(mergeEvent);
    }

    public void delete(final DeleteEvent deleteEvent) {
        deleteEventListener.onDelete(deleteEvent);
    }
}
