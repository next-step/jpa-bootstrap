package persistence.event;

import persistence.entity.EntityEntry;
import persistence.entity.EntityKey;
import persistence.entity.Status;

import java.io.Serializable;

public class DefaultLoadEventListener implements LoadEventListener {

    @Override
    public void onLoad(LoadEvent event) {
        final EventSource source = event.getSession();
        final Object entity = event.getEntity();
        final Serializable identifier = event.getIdentifier();
        final EntityEntry entry = event.getEntityEntry();

        final Class<?> entityClass = entity.getClass();
        final EntityKey entityKey = new EntityKey(identifier, entityClass);

        entry.updateStatus(Status.MANAGED);

        source.getPersistenceContext().addEntity(entityKey, entity);
        source.getPersistenceContext().addDatabaseSnapshot(entityKey, entity, source.getEntityPersister(entityClass));
        source.getPersistenceContext().addEntry(entityKey, entry);
    }
}
