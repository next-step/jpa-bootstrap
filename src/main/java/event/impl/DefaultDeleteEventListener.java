package event.impl;

import event.DeleteEventListener;
import event.Event;
import persistence.sql.clause.Clause;
import persistence.sql.context.EntityPersister;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.entity.EntityEntry;
import persistence.sql.entity.data.Status;
import persistence.sql.transaction.Transaction;

public class DefaultDeleteEventListener implements DeleteEventListener {

    @Override
    public void onDelete(DeleteEvent event) {
        EntityManager entityManager = event.entityManager();
        Object entity = event.entity();
        MetadataLoader<?> loader = event.metadataLoader();
        PersistenceContext persistenceContext = entityManager.getPersistenceContext();
        Transaction transaction = entityManager.getTransaction();

        Object id = Clause.extractValue(loader.getPrimaryKeyField(), entity);

        EntityEntry entityEntry = persistenceContext.getEntry(entity.getClass(), id);
        if (entityEntry == null) {
            throw new IllegalStateException("Entity not found. ");
        }

        entityEntry.updateStatus(Status.DELETED);
        if (!transaction.isActive()) {
            EntityPersister<?> entityPersister = entityManager.getEntityPersister(entity.getClass());
            entityPersister.delete(entity);
            persistenceContext.deleteEntry(entity, id);
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof DeleteEvent) {
            onDelete((DeleteEvent) event);
        }
    }
}
