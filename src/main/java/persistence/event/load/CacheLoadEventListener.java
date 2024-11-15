package persistence.event.load;

import persistence.entity.manager.factory.PersistenceContext;

public class CacheLoadEventListener implements LoadEventListener {
    @Override
    public <T> void onLoad(LoadEvent<T> loadEvent) {
        final PersistenceContext persistenceContext = loadEvent.getPersistenceContext();
        final Class<T> entityType = loadEvent.getEntityType();
        final Object id = loadEvent.getId();

        final T managedEntity = persistenceContext.getEntity(entityType, id);
        if (managedEntity != null) {
            loadEvent.setResult(managedEntity);
        }
    }
}
