package persistence.event.delete;

import persistence.action.ActionQueue;
import persistence.action.DeleteAction;
import persistence.bootstrap.Metamodel;
import persistence.entity.manager.EntityEntry;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.event.Event;

public class DefaultDeleteEventListener implements DeleteEventListener {
    public static final String NOT_REMOVABLE_STATUS_FAILED_MESSAGE = "엔티티가 제거 가능한 상태가 아닙니다.";

    private final Metamodel metamodel;
    private final PersistenceContext persistenceContext;
    private final ActionQueue actionQueue;

    public DefaultDeleteEventListener(Metamodel metamodel, PersistenceContext persistenceContext, ActionQueue actionQueue) {
        this.metamodel = metamodel;
        this.persistenceContext = persistenceContext;
        this.actionQueue = actionQueue;
    }

    @Override
    public <T> void on(Event<T> event) {
        final T entity = event.getEntity();

        final EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        if (!entityEntry.isRemovable()) {
            throw new IllegalStateException(NOT_REMOVABLE_STATUS_FAILED_MESSAGE);
        }

        actionQueue.addAction(new DeleteAction<>(metamodel, entity));
    }
}
