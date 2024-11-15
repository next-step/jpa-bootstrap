package persistence.event.clear;

import persistence.action.ActionQueue;
import persistence.entity.manager.factory.PersistenceContext;

public class DefaultClearEventListener implements ClearEventListener {
    @Override
    public void onClear(ClearEvent clearEvent) {
        final PersistenceContext persistenceContext = clearEvent.getPersistenceContext();
        final ActionQueue actionQueue = clearEvent.getActionQueue();

        persistenceContext.clear();
        actionQueue.clear();
    }
}
