package persistence.entity;

import persistence.entity.event.delete.DeleteEvent;
import persistence.entity.event.update.UpdateEvent;
import persistence.jpa.Cache;
import persistence.jpa.SnapShot;

import java.util.*;

public class HibernatePersistContext implements PersistenceContext {

    private final Cache cache;
    private final SnapShot snapshot;
    private final Map<EntityKey, EntityEntry> entityEntries;

    public HibernatePersistContext() {
        this.cache = new Cache();
        this.snapshot = new SnapShot();
        this.entityEntries = new HashMap<>();
    }

    @Override
    public <T> Optional<T> getEntity(Class<T> clazz, Object id) {
        EntityKey entityKey = new EntityKey(clazz, id);
        entityEntries.put(entityKey, new EntityEntry(EntityStatus.MANAGED));
        return (Optional<T>) cache.get(entityKey);
    }

    @Override
    public void addEntity(Object entity, Object id) {
        EntityKey entityKey = new EntityKey(entity.getClass(), id);
        entityEntries.put(entityKey, new EntityEntry(EntityStatus.SAVING));
        cache.save(entityKey, entity);
    }

    @Override
    public void removeEntity(Class<?> clazz, Object id) {
        EntityKey entityKey = new EntityKey(clazz, id);
        entityEntries.put(entityKey, new EntityEntry(EntityStatus.DELETED));
        cache.remove(entityKey);
    }

    @Override
    public EntityMetaData getDatabaseSnapshot(EntityMetaData entity, Object id) {
        return snapshot.save(new EntityKey(entity.getClazz(), id), entity);
    }

    @Override
    public <T> EntityMetaData getSnapshot(T entity, Object id) {
        return snapshot.get(new EntityKey(entity.getClass(), id));
    }

    @Override
    public void clear() {
        cache.clear();
        snapshot.clear();
        entityEntries.clear();
    }

}
