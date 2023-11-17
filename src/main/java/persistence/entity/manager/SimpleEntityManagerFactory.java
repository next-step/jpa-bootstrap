package persistence.entity.manager;

import persistence.core.EntityScanner;
import persistence.core.MetaModelFactory;
import persistence.core.PersistenceEnvironment;

public class SimpleEntityManagerFactory implements EntityManagerFactory {
    private final MetaModelFactory metaModelFactory;

    public SimpleEntityManagerFactory(final EntityScanner entityScanner, final PersistenceEnvironment persistenceEnvironment) {
        metaModelFactory = new MetaModelFactory(entityScanner, persistenceEnvironment);
    }

    @Override
    public EntityManager openSession() {
        return new SimpleEntityManager(metaModelFactory.createMetaModel());
    }
}

