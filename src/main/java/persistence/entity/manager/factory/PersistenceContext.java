package persistence.entity.manager.factory;

import persistence.entity.manager.EntityEntry;
import persistence.entity.manager.EntityKey;
import persistence.entity.manager.EntityStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceContext {
    private final Map<EntityKey, Object> entityRegistry = new ConcurrentHashMap<>();
    private final Map<EntityKey, Object> entitySnapshotRegistry = new ConcurrentHashMap<>();
    private final Map<Object, EntityEntry> entityEntryRegistry = new ConcurrentHashMap<>();

    public void addEntity(Object entity, Object id) {
        final EntityKey entityKey = new EntityKey(entity.getClass(), id);
        entityRegistry.put(entityKey, entity);
        addSnapshot(entity, entityKey);
    }

    public <T> T getEntity(Class<T> entityType, Object id) {
        final EntityKey entityKey = new EntityKey(entityType, id);
        return entityType.cast(entityRegistry.get(entityKey));
    }

    public void removeEntity(Object entity, Object id) {
        final EntityKey entityKey = new EntityKey(entity.getClass(), id);
        entityRegistry.remove(entityKey);
        entitySnapshotRegistry.remove(entityKey);
        createOrUpdateStatus(entity, EntityStatus.GONE);
    }

    public <T> T getSnapshot(Class<T> entityType, Object id) {
        final EntityKey entityKey = new EntityKey(entityType, id);
        return entityType.cast(entitySnapshotRegistry.get(entityKey));
    }

    public List<Object> getAllEntity() {
        return new ArrayList<>(entityRegistry.values());
    }

    public EntityEntry getEntityEntry(Object entity) {
        return entityEntryRegistry.get(entity);
    }

    public void createOrUpdateStatus(Object entity, EntityStatus entityStatus) {
        final EntityEntry entityEntry = entityEntryRegistry.get(entity);
        if (entityEntry == null) {
            entityEntryRegistry.put(entity, new EntityEntry(entityStatus));
            return;
        }
        entityEntry.updateStatus(entityStatus);
    }

    public void clear() {
        entityRegistry.clear();
        entitySnapshotRegistry.clear();
        entityEntryRegistry.clear();
    }

    private void addSnapshot(Object entity, EntityKey entityKey) {
        final Object snapshot = new InstanceFactory<>(entity.getClass()).copy(entity);
        entitySnapshotRegistry.put(entityKey, snapshot);
    }
}
