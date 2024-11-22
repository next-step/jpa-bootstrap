package event.impl;

import event.DeleteEventListener;
import persistence.sql.clause.Clause;
import persistence.sql.context.EntityPersister;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.entity.EntityEntry;
import persistence.sql.entity.data.Status;

public class DefaultDeleteEventListener<T> extends DeleteEventListener<T> {

    @Override
    public void onDelete(DeleteEvent<T> event) {
        EntityManager entityManager = event.entityManager();
        T entity = event.entity();
        MetadataLoader<T> loader = event.metadataLoader();
        PersistenceContext persistenceContext = entityManager.getPersistenceContext();
        EntityPersister<T> entityPersister = entityManager.getEntityPersister(loader.getEntityType());

        Object id = Clause.extractValue(loader.getPrimaryKeyField(), entity);

        EntityEntry entityEntry = persistenceContext.getEntry(entity.getClass(), id);
        entityManager.addDeletionAction(new EntityDeleteAction<>(entity, entityPersister));

        entityEntry.updateStatus(Status.DELETED);
    }
}
