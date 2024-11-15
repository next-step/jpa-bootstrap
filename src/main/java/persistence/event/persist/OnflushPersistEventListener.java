package persistence.event.persist;

import persistence.action.ActionQueue;
import persistence.action.PersistAction;
import persistence.bootstrap.Metamodel;
import persistence.entity.manager.factory.PersistenceContext;

public class OnflushPersistEventListener implements PersistEventListener {
    @Override
    public <T> void onPersist(PersistEvent<T> persistEvent) {
        final Metamodel metamodel = persistEvent.getMetamodel();
        final PersistenceContext persistenceContext = persistEvent.getPersistenceContext();
        final ActionQueue actionQueue = persistEvent.getActionQueue();
        final T entity = persistEvent.getEntity();

        actionQueue.addAction(new PersistAction<>(metamodel, persistenceContext, entity));
    }
}
