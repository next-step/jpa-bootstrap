package persistence.entity.context;

import persistence.bootstrap.Metadata;

public class PersistenceContextImpl implements PersistenceContext {
    private final Metadata metadata;
    private final FirstLevelCache firstLevelCache;
    private final EntityEntries entityEntries;

    public PersistenceContextImpl(Metadata metadata, FirstLevelCache firstLevelCache, EntityEntries entityEntries) {
        this.metadata = metadata;
        this.firstLevelCache = firstLevelCache;
        this.entityEntries = entityEntries;
    }

    @Override
    public <T> Object getEntity(PersistentClass<T> persistentClass, Long id) {
        EntityKey entityKey = EntityKey.of(persistentClass, id);

        if (!entityEntries.isReadable(entityKey)) return null;
        return firstLevelCache.find(entityKey);
    }

    @Override
    public void addEntity(Object entity) {
        EntityKey entityKey = getEntityKey(entity);

        if (entityEntries.isAssignable(entityKey)) {
            firstLevelCache.store(entityKey, entity);
            entityEntries.managed(entityKey);
        }
    }

    @Override
    public boolean isRemoved(Object entity) {
        EntityKey entityKey = getEntityKey(entity);

        return entityEntries.isRemoved(entityKey);
    }

    @Override
    public void removeEntity(Object entity) {
        EntityKey entityKey = getEntityKey(entity);

        if (entityEntries.isRemovable(entityKey)) {
            entityEntries.deleted(entityKey);
            firstLevelCache.delete(entityKey);
            entityEntries.gone(entityKey);
        }
    }

    private EntityKey getEntityKey(Object entity) {
        return EntityKey.of(entity.getClass(), metadata.getRowId(entity));
    }
}
