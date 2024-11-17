package persistence.event.delete;

import persistence.action.ActionQueue;
import persistence.action.DeleteAction;
import persistence.bootstrap.Metamodel;
import persistence.entity.manager.EntityEntry;
import persistence.entity.manager.factory.PersistenceContext;

public class DefaultDeleteEventListener implements DeleteEventListener {
    public static final String NOT_REMOVABLE_STATUS_FAILED_MESSAGE = "엔티티가 제거 가능한 상태가 아닙니다.";

    @Override
    public <T> void onDelete(DeleteEvent<T> deleteEvent) {
        final Metamodel metamodel = deleteEvent.getMetamodel();
        final PersistenceContext persistenceContext = deleteEvent.getPersistenceContext();
        final ActionQueue actionQueue = deleteEvent.getActionQueue();
        final T entity = deleteEvent.getEntity();

        final EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        if (!entityEntry.isRemovable()) {
            throw new IllegalStateException(NOT_REMOVABLE_STATUS_FAILED_MESSAGE);
        }

        actionQueue.addAction(new DeleteAction<>(metamodel, entity));
    }
}
