package persistence.event;


import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersisters;

public class EventListenerGroup {

    private final PersistEventListener persistEventListener;
    private final MergeEventListener mergeEventListener;
    private final DeleteEventListener deleteEventListener;
    private final LoadEventListener loadEventListener;

    public EventListenerGroup(final EntityPersisters entityPersisters, final EntityLoaders entityLoaders) {
        this.persistEventListener = new DefaultPersisEventListener(entityPersisters);
        this.mergeEventListener = new DefaultMergeEventListener(entityPersisters);
        this.deleteEventListener = new DefaultDeleteEventListener(entityPersisters);
        this.loadEventListener = new DefaultLoadEventListener(entityLoaders);
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

    public <T> T load(final LoadEvent<T> loadEvent) {
        return loadEventListener.onLoad(loadEvent);
    }
}
