package persistence.sql.context.impl;

import event.impl.EntityDeleteAction;
import event.impl.EntityInsertAction;
import event.impl.EntityUpdateAction;
import persistence.sql.context.CollectionKeyHolder;
import persistence.sql.context.EntityPersister;
import persistence.sql.context.KeyHolder;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.entity.CollectionEntry;
import persistence.sql.entity.EntityEntry;
import persistence.sql.entity.data.Status;

import java.util.HashMap;
import java.util.Map;

public class DefaultPersistenceContext implements PersistenceContext {
    private final Map<KeyHolder, EntityEntry> context = new HashMap<>();
    private final Map<CollectionKeyHolder, CollectionEntry> collectionContext = new HashMap<>();

    @Override
    public EntityEntry addEntry(Object entity, Status status, EntityPersister<?> entityPersister) {
        EntityEntry entityEntry = EntityEntry.newEntry(entity, status, entityPersister.getMetadataLoader());
        context.put(entityEntry.getKey(), entityEntry);

        entityEntry.updateStatus(Status.MANAGED);
        return entityEntry;
    }

    @Override
    public EntityEntry addLoadingEntry(Object primaryKey, MetadataLoader<?> metadataLoader) {
        EntityEntry entityEntry = EntityEntry.newLoadingEntry(primaryKey, metadataLoader);
        context.put(entityEntry.getKey(), entityEntry);

        return entityEntry;
    }

    @Override
    public <ID> EntityEntry getEntry(Class<?> entityType, ID id) {
        EntityEntry entry = getEntryOrNull(entityType, id);
        if (entry != null) {
            return entry;
        }

        throw new IllegalArgumentException("Not found entity entry");
    }

    @Override
    public <ID> EntityEntry getEntryOrNull(Class<?> entityType, ID id) {
        KeyHolder key = new KeyHolder(entityType, id);

        EntityEntry entityEntry = context.get(key);
        if (entityEntry != null && entityType.isAssignableFrom(entityEntry.getEntityType())) {
            return entityEntry;
        }

        return null;
    }

    @Override
    public CollectionEntry getCollectionEntry(CollectionKeyHolder keyHolder) {
        if (collectionContext.containsKey(keyHolder)) {
            return collectionContext.get(keyHolder);
        }

        return null;
    }

    @Override
    public CollectionEntry addCollectionEntry(CollectionKeyHolder keyHolder, CollectionEntry collectionEntry) {
        collectionContext.put(keyHolder, collectionEntry);

        return collectionEntry;
    }

    @Override
    public <ID> void deleteEntry(Object entity, ID id) {
        KeyHolder key = new KeyHolder(entity.getClass(), id);
        context.remove(key);
    }

    @Override
    public void dirtyCheck(EntityManager entityManager) {
        for (EntityEntry entry : context.values()) {
            EntityPersister<?> persister = entityManager.getEntityPersister(entry.getEntityType());
            handleEntry(persister, entry, entityManager);
        }
    }

    private void handleEntry(EntityPersister<?> persister, EntityEntry entry, EntityManager entityManager) {
        switch (entry.getStatus()) {
            case SAVING:
                handleSavingEntry(persister, entry, entityManager);
                break;
            case MANAGED:
                handleUpdateEntry(persister, entry, entityManager);
                break;
            case DELETED:
                handleDeleteEntry(persister, entry, entityManager);
                break;
        }
    }

    private void handleSavingEntry(EntityPersister<?> persister, EntityEntry entry, EntityManager entityManager) {
        EntityInsertAction<?> action = EntityInsertAction.create(persister, entry.getEntity(), entry.getEntityType());
        entityManager.addInsertionAction(action);
        entry.updateStatus(Status.MANAGED);
        entry.synchronizingSnapshot();
    }

    private void handleUpdateEntry(EntityPersister<?> persister, EntityEntry entry, EntityManager entityManager) {
        if (!entry.isDirty()) {
            return;
        }
        EntityUpdateAction<?> action = EntityUpdateAction.create(persister, entry.getEntity(), entry.getSnapshot(), entry.getEntityType());
        entityManager.addUpdateAction(action);
        entry.synchronizingSnapshot();
    }

    private void handleDeleteEntry(EntityPersister<?> persister, EntityEntry entry, EntityManager entityManager) {
        EntityDeleteAction<?> action = EntityDeleteAction.create(persister, entry.getEntity(), entry.getEntityType());
        entityManager.addDeletionAction(action);
        entry.updateStatus(Status.GONE);
        context.remove(entry.getKey());
    }

    @Override
    public void cleanup() {
        context.clear();
    }
}
