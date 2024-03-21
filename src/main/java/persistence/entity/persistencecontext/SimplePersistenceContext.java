package persistence.entity.persistencecontext;

import java.util.HashMap;
import java.util.Map;
import persistence.entity.entitymanager.EntityEntry;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> entities;
    private final Map<EntityKey, EntitySnapshot> snapshot;
    private final Map<EntityKey, EntityEntry> entityEntryMap;

    public SimplePersistenceContext() {
        this.entities = new HashMap<>();
        this.snapshot = new HashMap<>();
        this.entityEntryMap = new HashMap<>();
    }

    @Override
    public <T> T getEntity(Class<T> clazz, Object id) {
        EntityKey key = EntityKey.of(clazz, id);
        if (entities.containsKey(key)) {
            return (T) entities.get(key);
        }
        return null;
    }

    @Override
    public <T> void addEntity(T entity) {
        EntityKey key = EntityKey.from(entity);
        entities.put(key, entity);
    }

    @Override
    public <T> void removeEntity(T entity) {
        EntityKey key = EntityKey.from(entity);

        entities.remove(key);
    }

    @Override
    public EntitySnapshot getCachedDatabaseSnapshot(Object entity) {
        return snapshot.get(EntityKey.from(entity));
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(Object entity) {
        return snapshot.computeIfAbsent(EntityKey.from(entity), k -> EntitySnapshot.from(entity));
    }

    @Override
    public <T> void setEntityEntry(T entity, EntityEntry entityEntry) {
        entityEntryMap.put(EntityKey.from(entity), entityEntry);
    }

    @Override
    public <T> EntityEntry getEntityEntry(T entity) {
        return entityEntryMap.get(EntityKey.from(entity));
    }
}
