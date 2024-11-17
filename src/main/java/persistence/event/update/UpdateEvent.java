package persistence.event.update;

import persistence.action.ActionQueue;
import persistence.bootstrap.Metamodel;
import persistence.entity.manager.factory.PersistenceContext;

public class UpdateEvent<T> {
    private final Metamodel metamodel;
    private final PersistenceContext persistenceContext;
    private final ActionQueue actionQueue;
    private final T entity;

    public UpdateEvent(Metamodel metamodel, PersistenceContext persistenceContext, ActionQueue actionQueue, T entity) {
        this.metamodel = metamodel;
        this.persistenceContext = persistenceContext;
        this.actionQueue = actionQueue;
        this.entity = entity;
    }

    public Metamodel getMetamodel() {
        return metamodel;
    }

    public PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }

    public ActionQueue getActionQueue() {
        return actionQueue;
    }

    public T getEntity() {
        return entity;
    }
}
