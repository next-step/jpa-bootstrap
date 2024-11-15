package persistence.event.flush;

import persistence.action.ActionQueue;
import persistence.bootstrap.Metamodel;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.event.update.UpdateEvent;
import persistence.event.update.UpdateEventListener;

public class DefaultFlushEventListener implements FlushEventListener {
    @Override
    public void onFlush(FlushEvent flushEvent) {
        final Metamodel metamodel = flushEvent.getMetamodel();
        final PersistenceContext persistenceContext = flushEvent.getPersistenceContext();
        final ActionQueue actionQueue = flushEvent.getActionQueue();

        actionQueue.executeAll();
        updateAllEntity(metamodel, persistenceContext, actionQueue);
    }

    private void updateAllEntity(Metamodel metamodel, PersistenceContext persistenceContext, ActionQueue actionQueue) {
        persistenceContext.getAllEntity()
                .forEach(entity -> update(entity, metamodel, persistenceContext, actionQueue));
    }

    private <T> void update(T entity, Metamodel metamodel, PersistenceContext persistenceContext, ActionQueue actionQueue) {
        final UpdateEvent<T> updateEvent = new UpdateEvent<>(metamodel, persistenceContext, actionQueue, entity);
        metamodel.getUpdateEventListenerGroup().doEvent(updateEvent, UpdateEventListener::onUpdate);
    }
}
