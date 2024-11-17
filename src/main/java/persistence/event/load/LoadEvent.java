package persistence.event.load;

import persistence.bootstrap.Metamodel;
import persistence.entity.manager.factory.PersistenceContext;

public class LoadEvent<T> {
    private final Metamodel metamodel;
    private final PersistenceContext persistenceContext;
    private final Class<T> entityType;
    private final Object id;
    private T result;

    public LoadEvent(Metamodel metamodel, PersistenceContext persistenceContext, Class<T> entityType, Object id) {
        this.metamodel = metamodel;
        this.persistenceContext = persistenceContext;
        this.entityType = entityType;
        this.id = id;
    }

    public Metamodel getMetamodel() {
        return metamodel;
    }

    public PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public Object getId() {
        return id;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
