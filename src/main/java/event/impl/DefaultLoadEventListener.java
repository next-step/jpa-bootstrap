package event.impl;

import event.LoadEventListener;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.entity.EntityEntry;
import persistence.sql.entity.data.Status;
import persistence.sql.loader.EntityLoader;
import persistence.sql.transaction.Transaction;

public class DefaultLoadEventListener<T> extends LoadEventListener<T> {

    @Override
    public void onLoad(LoadEvent<T> event) {
        EntityManager entityManager = event.entityManager();
        PersistenceContext persistenceContext = entityManager.getPersistenceContext();
        Transaction transaction = entityManager.getTransaction();
        MetadataLoader<T> originLoader = event.metadataLoader();
        Object primaryKey = event.entityId();
        Class<T> returnType = originLoader.getEntityType();

        EntityEntry entry = persistenceContext.getEntryOrNull(returnType, primaryKey);

        if (entry != null) {
            return;
        }

        EntityLoader<T> entityLoader = entityManager.getEntityLoader(returnType);
        entry = persistenceContext.addLoadingEntry(primaryKey, entityLoader.getMetadataLoader());

        T loadedEntity = entityLoader.load(primaryKey);
        if (loadedEntity != null) {
            entry.updateEntity(loadedEntity);
            entry.updateStatus(Status.MANAGED);
        }

        if (entityLoader.existLazyLoading()) {
            entityLoader.updateLazyLoadingField(loadedEntity, persistenceContext, entityManager::getEntityLoader,
                    (collectionKeyHolder, collectionEntry) -> {
                        if (transaction.isActive()) {
                            persistenceContext.addCollectionEntry(collectionKeyHolder, collectionEntry);
                        }
                    });
        }
    }
}
