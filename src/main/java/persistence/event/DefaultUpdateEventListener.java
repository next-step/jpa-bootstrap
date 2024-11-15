package persistence.event;

import persistence.bootstrap.Metamodel;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.entity.persister.EntityPersister;
import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;

import java.util.List;

public class DefaultUpdateEventListener implements UpdateEventListener {
    @Override
    public <T> void onUpdate(UpdateEvent<T> updateEvent) {
        final Metamodel metamodel = updateEvent.getMetamodel();
        final PersistenceContext persistenceContext = updateEvent.getPersistenceContext();
        final T entity = updateEvent.getEntity();

        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());
        final T snapshot = (T) persistenceContext.getSnapshot(entity.getClass(), entityTable.getIdValue(entity));
        if (snapshot == null) {
            return;
        }

        final DirtyCheckEvent<T> dirtyCheckEvent = new DirtyCheckEvent<>(metamodel, entity, snapshot);
        metamodel.getDirtyCheckEventListenerGroup().doEvent(dirtyCheckEvent, DirtyCheckEventListener::onDirtyCheck);

        final List<EntityColumn> dirtiedEntityColumns = dirtyCheckEvent.getResult();
        if (dirtiedEntityColumns.isEmpty()) {
            return;
        }

        final EntityPersister entityPersister = metamodel.getEntityPersister(entity.getClass());
        entityPersister.update(entity, dirtiedEntityColumns);
        persistenceContext.addEntity(entity, entityTable);
    }
}
