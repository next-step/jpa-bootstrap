package persistence.entity.context;

public class PersistenceContextImpl implements PersistenceContext {
    private final FirstLevelCache firstLevelCache;
    private final EntityEntries entityEntries;

    public PersistenceContextImpl() {
        firstLevelCache = new FirstLevelCache();
        entityEntries = new EntityEntries();
    }

    @Override
    public <T> Object getEntity(PersistentClass<T> persistentClass, Long id) {
        EntityKey entityKey = EntityKey.of(persistentClass, id);

        if (!entityEntries.isReadable(entityKey)) return null;
        return firstLevelCache.find(entityKey);
    }

    @Override
    public void addEntity(Object entity) {
        EntityKey entityKey = EntityKey.of(entity);

        if (entityEntries.isAssignable(entityKey)) {
            firstLevelCache.store(entityKey, entity);
            entityEntries.managed(entityKey);
        }
    }

    @Override
    public boolean isRemoved(Object entity) {
        EntityKey entityKey = EntityKey.of(entity);

        return entityEntries.isRemoved(entityKey);
    }

    @Override
    public void removeEntity(Object entity) {
        EntityKey entityKey = EntityKey.of(entity);

        if (entityEntries.isRemovable(entityKey)) {
            entityEntries.deleted(entityKey);
            firstLevelCache.delete(entityKey);
            entityEntries.gone(entityKey);
        }
    }
}
