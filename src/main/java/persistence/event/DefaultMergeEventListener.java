package persistence.event;

import persistence.entity.EntityKey;
import persistence.entity.EntityPersister;
import persistence.entity.EntitySnapshot;
import persistence.entity.Status;

public class DefaultMergeEventListener implements MergeEventListener {

    @Override
    public void onMerge(MergeEvent event) {
        final Object entity = event.getEntity();
        final EntityPersister persister = event.getSession().findEntityPersister(entity.getClass());
        final EntityKey entityKey = new EntityKey(persister.getEntityId(entity), entity.getClass());

        final EntitySnapshot snapshot = event.getSession().getPersistenceContext().getDatabaseSnapshot(entityKey);
        if (snapshot.hasDirtyColumns(entity, persister)) {
            persister.update(event.getEntity());
        }

        event.getEntry().updateStatus(Status.MANAGED);

        event.getSession().getPersistenceContext().addEntity(entityKey, entity);
        event.getSession().getPersistenceContext().addDatabaseSnapshot(entityKey, entity, persister);
        event.getSession().getPersistenceContext().addEntry(entityKey, event.getEntry());
    }
}
