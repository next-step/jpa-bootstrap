package persistence.entity.manager;

import persistence.core.EntityScanner;
import persistence.core.MetaModelFactory;
import persistence.core.PersistenceEnvironment;

public class SimpleEntityManagerFactory implements EntityManagerFactory {
    private final MetaModelFactory metaModelFactory;
    private final CurrentSessionContext currentSessionContext;

    public SimpleEntityManagerFactory(final EntityScanner entityScanner, final PersistenceEnvironment persistenceEnvironment) {
        this.metaModelFactory = new MetaModelFactory(entityScanner, persistenceEnvironment);
        this.currentSessionContext = new ThreadLocalCurrentSessionContext();
    }

    @Override
    public EntityManager openSession() {
        return currentSessionContext.getCurrentSession()
                .orElseGet(this::createEntityManager);
    }

    private EntityManager createEntityManager() {
        final EntityManager manager = new SimpleEntityManager(metaModelFactory.createMetaModel(), currentSessionContext);
        currentSessionContext.open(manager);
        return manager;
    }

}

