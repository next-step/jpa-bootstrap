package persistence.event.merge;

import persistence.action.ActionQueue;
import persistence.bootstrap.Metamodel;
import persistence.entity.loader.EntityLoader;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.event.persist.PersistEvent;
import persistence.event.persist.PersistEventListener;
import persistence.event.update.UpdateEvent;
import persistence.event.update.UpdateEventListener;
import persistence.meta.EntityTable;

public class DefaultMergeEventListener implements MergeEventListener {
    @Override
    public <T> void onMerge(MergeEvent<T> mergeEvent) {
        final Metamodel metamodel = mergeEvent.getMetamodel();
        final PersistenceContext persistenceContext = mergeEvent.getPersistenceContext();
        final ActionQueue actionQueue = mergeEvent.getActionQueue();
        final T entity = mergeEvent.getEntity();

        final EntityLoader entityLoader = metamodel.getEntityLoader(entity.getClass());
        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());

        final Object managedEntity = entityLoader.load(entityTable.getIdValue(entity));
        if (managedEntity == null) {
            final PersistEvent<T> persistEvent = new PersistEvent<>(metamodel, persistenceContext, actionQueue, entity);
            metamodel.getPersistEventListenerGroup().doEvent(persistEvent, PersistEventListener::onPersist);
        }

        final UpdateEvent<T> updateEvent = new UpdateEvent<>(metamodel, persistenceContext, actionQueue, entity);
        metamodel.getUpdateEventListenerGroup().doEvent(updateEvent, UpdateEventListener::onUpdate);
    }
}
