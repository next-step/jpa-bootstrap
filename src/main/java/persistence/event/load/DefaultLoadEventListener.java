package persistence.event.load;

import persistence.bootstrap.Metamodel;
import persistence.entity.loader.EntityLoader;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.event.Event;

public class DefaultLoadEventListener implements LoadEventListener {
    private final Metamodel metamodel;
    private final PersistenceContext persistenceContext;

    public DefaultLoadEventListener(Metamodel metamodel, PersistenceContext persistenceContext) {
        this.metamodel = metamodel;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> void on(Event<T> event) {
        final Class<T> entityType = event.getEntityType();
        final Object id = event.getId();

        final T managedEntity = persistenceContext.getEntity(entityType, id);
        if (managedEntity != null) {
            event.setResult(managedEntity);
            return;
        }

        final EntityLoader entityLoader = metamodel.getEntityLoader(entityType);
        final T result = entityLoader.load(id);
        event.setResult(result);
    }
}
