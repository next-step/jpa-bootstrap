package persistence.entity.impl.context;

import java.util.Optional;
import persistence.entity.EntityEntry;
import persistence.entity.EventSource;
import persistence.entity.PersistenceContext;
import persistence.entity.impl.SnapShot;
import persistence.sql.schema.meta.EntityObjectMappingMeta;
import registry.EntityMetaRegistry;

public class DefaultPersistenceContext implements PersistenceContext, EventSource {

    private final EntityMetaRegistry entityMetaRegistry;
    private final PersistenceCacheContext persistenceCacheContext;
    private final SnapShotCacheContext snapShotCacheContext;
    private final EntityEntryContext entityEntryContext;

    public DefaultPersistenceContext(EntityMetaRegistry entityMetaRegistry) {
        this.entityMetaRegistry = entityMetaRegistry;
        this.persistenceCacheContext = PersistenceCacheContext.of(entityMetaRegistry);
        this.snapShotCacheContext = SnapShotCacheContext.of(entityMetaRegistry);
        this.entityEntryContext = new EntityEntryContext();
    }

    @Override
    public Optional<Object> getEntity(Class<?> entityClazz, Object id) {
        return persistenceCacheContext.tryGetEntityCache(entityClazz, id);
    }

    @Override
    public void addEntity(Object entity) {
        persistenceCacheContext.putEntityCache(entity);
    }

    @Override
    public void removeEntity(Object entity) {
        persistenceCacheContext.purgeEntityCache(entity);
    }

    @Override
    public Object getDatabaseSnapshot(Object id, Object entity) {
        return snapShotCacheContext.putSnapShotCache(id, entity);
    }

    @Override
    public void purgeEntityCache(Object entity) {
        snapShotCacheContext.purgeSnapShotCache(entity);
    }

    @Override
    public SnapShot getSnapShot(Class<?> clazz, Object id) {
        return snapShotCacheContext.getSnapShotCache(clazz, id);
    }

    @Override
    public void clearContextCache() {
        persistenceCacheContext.clear();
        snapShotCacheContext.clear();
        entityEntryContext.clear();
    }

    /**
     * EventSource implement
     */

    @Override
    public void putEntity(Object entity) {
        final EntityObjectMappingMeta entityObjectMappingMeta = EntityObjectMappingMeta.of(
            entity,
            entityMetaRegistry.getEntityMeta(entity.getClass())
        );

        this.addEntity(entity);
        this.getDatabaseSnapshot(entityObjectMappingMeta.getIdValue(), entity);
        this.managed(entity);
    }

    @Override
    public void purgeEntity(Object entity) {
        this.removeEntity(entity);
        this.purgeEntityCache(entity);
        this.gone(entity);
    }

    @Override
    public void loading(Object object) {
        entityEntryContext.loading(object);
    }

    @Override
    public void saving(Object object) {
        entityEntryContext.saving(object);
    }

    @Override
    public void managed(Object object) {
        entityEntryContext.managed(object);
    }

    @Override
    public void readOnly(Object object) {
        entityEntryContext.readOnly(object);
    }

    @Override
    public void deleted(Object object) {
        entityEntryContext.deleted(object);
    }

    @Override
    public void gone(Object object) {
        entityEntryContext.gone(object);
    }

    @Override
    public EntityEntry getEntityEntry(Object object) {
        return entityEntryContext.getEntityEntry(object);
    }
}
