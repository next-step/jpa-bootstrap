package persistence.event;

import persistence.bootstrap.Metamodel;
import persistence.entity.manager.factory.PersistenceContext;

public class PersistEvent<T> {
    private final Metamodel metamodel;
    private final PersistenceContext persistenceContext;
    private T entity;

    public PersistEvent(Metamodel metamodel, PersistenceContext persistenceContext, T entity) {
        this.metamodel = metamodel;
        this.persistenceContext = persistenceContext;
        this.entity = entity;
    }

    public Metamodel getMetamodel() {
        return metamodel;
    }

    public PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }

    public T getEntity() {
        return entity;
    }
}
