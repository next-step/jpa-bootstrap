package persistence.event.load;

import persistence.entity.EntityEntry;
import persistence.entity.EntityKey;
import persistence.entity.EntityLoader;
import persistence.entity.Status;
import persistence.event.EventSource;

import java.io.Serializable;

public class DefaultLoadEventListener implements LoadEventListener {

    @Override
    public <T> void onLoad(LoadEvent<T> event) {
        final EventSource source = event.getSession();
        final Class<T> entityClass = event.getEntityClass();
        final EntityLoader loader = source.findEntityLoader(entityClass);
        final EntityKey entityKey = new EntityKey(event.getIdentifier(), entityClass);
        final EntityEntry entry = event.getEntityEntry();

        final T entity = loader.loadEntity(entityClass, entityKey);

        entry.updateStatus(Status.MANAGED);
        source.getPersistenceContext().addEntity(entityKey, entity);
        source.getPersistenceContext().addDatabaseSnapshot(entityKey, entity, source.findEntityPersister(entityClass));
        source.getPersistenceContext().addEntry(entityKey, entry);

        event.setResultEntity(entity);
    }
}
