package persistence.entitymanager;

import persistence.bootstrap.Metadata;
import persistence.bootstrap.Metamodel;
import persistence.entity.context.EntityEntries;
import persistence.entity.context.FirstLevelCache;
import persistence.entity.context.PersistenceContext;
import persistence.entity.context.PersistenceContextImpl;
import persistence.entity.database.CollectionLoader;
import persistence.entity.database.EntityLoader;
import persistence.entity.database.EntityPersister;
import persistence.entitymanager.actionqueue.ActionQueue;

public abstract class AbstractEntityManager implements SessionContract, EntityManager, Session {
    protected final Metamodel metamodel;
    protected final ActionQueue actionQueue;
    protected final PersistenceContext persistenceContext;

    protected AbstractEntityManager(Metamodel metamodel, Metadata metadata) {
        this.metamodel = metamodel;

        this.persistenceContext = new PersistenceContextImpl(
                metadata,
                new FirstLevelCache(),
                new EntityEntries(),
                this);

        this.actionQueue = new ActionQueue(persistenceContext);
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

    @Override
    public void flush() {
        actionQueue.flush();
    }

    @Override
    public void clear() {
        actionQueue.clear();
    }

    @Override
    public ActionQueue getActionQueue() {
        return actionQueue;
    }

    @Override
    public PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }
}
