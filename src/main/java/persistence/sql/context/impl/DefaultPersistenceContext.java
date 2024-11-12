package persistence.sql.context.impl;

import boot.MetaModel;
import persistence.sql.context.CollectionKeyHolder;
import persistence.sql.context.EntityPersister;
import persistence.sql.context.KeyHolder;
import persistence.sql.context.PersistenceContext;
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
    @SuppressWarnings("unchecked")
    public <ID> EntityEntry getEntry(Class<?> entityType, ID id) {
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
    public void dirtyCheck(MetaModel metaModel) {
        for (EntityEntry entry : context.values()) {
            EntityPersister<?> persister = metaModel.entityPersister(entry.getEntity().getClass());
            handleEntry(persister, entry);
        }
    }

    private void handleEntry(EntityPersister<?> persister, EntityEntry entry) {
        switch (entry.getStatus()) {
            case SAVING:
                handleSavingEntry(persister, entry);
                break;
            case MANAGED:
                handleUpdateEntry(persister, entry);
                break;
            case DELETED:
                handleDeleteEntry(persister, entry);
                break;
        }
    }

    private void handleSavingEntry(EntityPersister<?> persister, EntityEntry entry) {
        persister.insert(entry.getEntity());
        entry.updateStatus(Status.MANAGED);
        entry.synchronizingSnapshot();
    }

    private void handleUpdateEntry(EntityPersister<?> persister, EntityEntry entry) {
        if (!entry.isDirty()) {
            return;
        }
        persister.update(entry.getEntity(), entry.getSnapshot());
        entry.synchronizingSnapshot();
    }

    private void handleDeleteEntry(EntityPersister<?> persister, EntityEntry entry) {
        persister.delete(entry.getEntity());
        entry.updateStatus(Status.GONE);
        context.remove(entry.getKey());
    }

    @Override
    public void cleanup() {
        context.clear();
    }
}
