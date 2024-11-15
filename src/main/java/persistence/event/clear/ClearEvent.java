package persistence.event.clear;

import persistence.action.ActionQueue;
import persistence.entity.manager.factory.PersistenceContext;

public class ClearEvent {
    private final PersistenceContext persistenceContext;
    private final ActionQueue actionQueue;

    public ClearEvent(PersistenceContext persistenceContext, ActionQueue actionQueue) {
        this.persistenceContext = persistenceContext;
        this.actionQueue = actionQueue;
    }

    public PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }

    public ActionQueue getActionQueue() {
        return actionQueue;
    }
}
