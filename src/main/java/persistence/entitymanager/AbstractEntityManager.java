package persistence.entitymanager;

import persistence.bootstrap.Metamodel;
import persistence.entity.database.CollectionLoader;
import persistence.entity.database.EntityLoader;
import persistence.entity.database.EntityPersister;

public abstract class AbstractEntityManager implements SessionContract, EntityManager {
    protected final Metamodel metamodel;

    protected AbstractEntityManager(Metamodel metamodel) {
        this.metamodel = metamodel;
    }

    @Override
    public <T> EntityPersister<T> getEntityPersister(Class<T> clazz) {
        return metamodel.getEntityPersister(clazz);
    }

    @Override
    public <T> EntityLoader<T> getEntityLoader(Class<T> clazz) {
        return metamodel.getEntityLoader(clazz);
    }

    @Override
    public <T> CollectionLoader<T> getCollectionLoader(Class<T> clazz) {
        return metamodel.getCollectionLoader(clazz);
    }
}
