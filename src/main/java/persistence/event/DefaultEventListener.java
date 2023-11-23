package persistence.event;

import persistence.action.ActionQueue;
import persistence.action.EntityDeleteAction;
import persistence.action.EntityInsertAction;
import persistence.action.EntityUpdateAction;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;

public class DefaultEventListener implements EventListener {

    private final ActionQueue actionQueue;
    private final EntityPersisters entityPersisters;
    private final EntityLoaders entityLoaders;

    public DefaultEventListener(final ActionQueue actionQueue, final EntityPersisters entityPersisters, final EntityLoaders entityLoaders) {
        this.actionQueue = actionQueue;
        this.entityPersisters = entityPersisters;
        this.entityLoaders = entityLoaders;
    }

    @Override
    public void onPersist(final PersistEvent persistEvent) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(persistEvent.getTargetClass());
        actionQueue.addInsertion(new EntityInsertAction(entityPersister, persistEvent.getTarget()));
    }

    @Override
    public void onMerge(final MergeEvent mergeEvent) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(mergeEvent.getTargetClass());
        actionQueue.addUpdate(new EntityUpdateAction(entityPersister, mergeEvent.getTarget()));
    }

    @Override
    public void onDelete(final DeleteEvent deleteEvent) {
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(deleteEvent.getTargetClass());
        actionQueue.addDeletion(new EntityDeleteAction(entityPersister, deleteEvent.getTarget()));
    }

    @Override
    public <T> T onLoad(final LoadEvent<T> loadEvent) {
        final EntityLoader<T> entityLoader = entityLoaders.getEntityLoader(loadEvent.getTargetClass());
        return entityLoader.loadById(loadEvent.getTarget()).orElse(null);
    }

}
