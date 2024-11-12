package persistence.event;

import persistence.entity.EntityEntry;
import persistence.entity.EntityPersister;

public class DefaultPersistEventListener extends AbstractPersistEventListener {

    @Override
    public void onPersist(PersistEvent event) {
        final EventSource source = event.getSession();
        final Object entity = event.getEntity();
        final EntityEntry entry = EntityEntry.inSaving();

        doPersist(source, entity, entry);
    }

    private void doPersist(EventSource source, Object entity, EntityEntry entry) {
        final EntityPersister persister = source.findEntityPersister(entity.getClass());
        persister.insert(entity);
        managePersistedEntity(source, persister, entity, entry);
    }

}
