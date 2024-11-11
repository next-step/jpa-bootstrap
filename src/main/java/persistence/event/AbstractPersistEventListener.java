package persistence.event;

import persistence.entity.EntityEntry;
import persistence.entity.EntityKey;
import persistence.entity.EntityPersister;
import persistence.entity.Status;

import java.io.Serializable;

public abstract class AbstractPersistEventListener implements PersistEventListener {

    protected void managePersistedEntity(EventSource source,
                                         EntityPersister persister,
                                         Object entity,
                                         EntityEntry entry) {
        // manage entity
        final Serializable identifier = persister.getEntityId(entity);
        final EntityKey entityKey = new EntityKey(identifier, entity.getClass());

        entry.bindId(identifier);
        entry.updateStatus(Status.MANAGED);

        source.getPersistenceContext().addEntity(entityKey, entity);
        source.getPersistenceContext().addDatabaseSnapshot(entityKey, entity, persister);
        source.getPersistenceContext().addEntry(entityKey, entry);
    }
}
