package persistence.event.update;

import persistence.action.ActionQueue;
import persistence.action.UpdateAction;
import persistence.bootstrap.Metamodel;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.event.dirtycheck.DirtyCheckEvent;
import persistence.event.dirtycheck.DirtyCheckEventListener;
import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;

import java.util.List;

public class DefaultUpdateEventListener implements UpdateEventListener {
    @Override
    public <T> void onUpdate(UpdateEvent<T> updateEvent) {
        final Metamodel metamodel = updateEvent.getMetamodel();
        final PersistenceContext persistenceContext = updateEvent.getPersistenceContext();
        final ActionQueue actionQueue = updateEvent.getActionQueue();
        final T entity = updateEvent.getEntity();

        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());
        final Object snapshot = persistenceContext.getSnapshot(entity.getClass(), entityTable.getIdValue(entity));
        if (snapshot == null) {
            return;
        }

        final DirtyCheckEvent<T> dirtyCheckEvent = new DirtyCheckEvent<>(metamodel, entity, snapshot);
        metamodel.getDirtyCheckEventListenerGroup().doEvent(dirtyCheckEvent, DirtyCheckEventListener::onDirtyCheck);

        final List<EntityColumn> dirtiedEntityColumns = dirtyCheckEvent.getResult();
        if (dirtiedEntityColumns.isEmpty()) {
            return;
        }

        actionQueue.addAction(new UpdateAction<>(metamodel, persistenceContext, entity, dirtiedEntityColumns));
    }
}
