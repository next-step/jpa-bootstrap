package persistence.event;

import persistence.bootstrap.Metamodel;
import persistence.entity.loader.EntityLoader;
import persistence.entity.manager.factory.PersistenceContext;

public class DefaultLoadEventListener implements LoadEventListener {
    @Override
    public <T> void onLoad(LoadEvent<T> loadEvent) {
        final Metamodel metamodel = loadEvent.getMetamodel();
        final PersistenceContext persistenceContext = loadEvent.getPersistenceContext();
        final Class<T> entityType = loadEvent.getEntityType();
        final Object id = loadEvent.getId();

        if (loadEvent.getResult() != null) {
            return;
        }

        final EntityLoader entityLoader = metamodel.getEntityLoader(entityType);
        final T result = entityLoader.load(id);
        persistenceContext.addEntity(result, id);
        loadEvent.setResult(result);
    }
}
