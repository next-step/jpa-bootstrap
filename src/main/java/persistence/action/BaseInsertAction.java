package persistence.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityEntry;
import persistence.entity.EntityKey;
import persistence.entity.EntityPersister;
import persistence.entity.Status;
import persistence.event.EventSource;

import java.io.Serializable;

public abstract class BaseInsertAction {

    private static final Logger logger = LoggerFactory.getLogger(BaseInsertAction.class);

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

        logger.info("""
                Entity with id {} and class {} has been managed.
                """, identifier, entity.getClass().getName());
    }
}
