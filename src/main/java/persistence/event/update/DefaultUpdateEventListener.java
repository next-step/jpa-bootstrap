package persistence.event.update;

import persistence.action.ActionQueue;
import persistence.action.UpdateAction;
import persistence.bootstrap.Metamodel;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.event.Event;
import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;

import java.util.List;
import java.util.Objects;

public class DefaultUpdateEventListener implements UpdateEventListener {
    private final Metamodel metamodel;
    private final PersistenceContext persistenceContext;
    private final ActionQueue actionQueue;

    public DefaultUpdateEventListener(Metamodel metamodel, PersistenceContext persistenceContext, ActionQueue actionQueue) {
        this.metamodel = metamodel;
        this.persistenceContext = persistenceContext;
        this.actionQueue = actionQueue;
    }

    @Override
    public <T> void on(Event<T> event) {
        final T entity = event.getEntity();

        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());
        final Object snapshot = persistenceContext.getSnapshot(entity.getClass(), entityTable.getIdValue(entity));
        final List<EntityColumn> dirtiedEntityColumns = entityTable.getEntityColumns()
                .stream()
                .filter(entityColumn -> isDirtied(entity, snapshot, entityColumn))
                .toList();

        if (dirtiedEntityColumns.isEmpty()) {
            return;
        }

        actionQueue.addAction(new UpdateAction<>(metamodel, persistenceContext, entity, dirtiedEntityColumns));
    }

    private boolean isDirtied(Object entity, Object snapshot, EntityColumn entityColumn) {
        return !Objects.equals(entityColumn.extractValue(entity), entityColumn.extractValue(snapshot));
    }
}
