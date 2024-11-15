package persistence.event.update;

import persistence.bootstrap.Metamodel;
import persistence.entity.manager.factory.PersistenceContext;

public class UpdateEvent<T> {
    private final Metamodel metamodel;
    private final PersistenceContext persistenceContext;
    private final T entity;

    public UpdateEvent(Metamodel metamodel, PersistenceContext persistenceContext, T entity) {
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
