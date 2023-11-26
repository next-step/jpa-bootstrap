package persistence.entity.impl.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import persistence.entity.impl.EntityIdentifier;
import persistence.sql.schema.meta.EntityClassMappingMeta;
import persistence.sql.schema.meta.EntityObjectMappingMeta;
import registry.EntityMetaRegistry;

public class PersistenceCacheContext {

    private final Map<EntityIdentifier, Object> contextCacheMap;
    private final EntityMetaRegistry entityMetaRegistry;

    private PersistenceCacheContext(EntityMetaRegistry entityMetaRegistry, Map<EntityIdentifier, Object> contextCacheMap) {
        this.entityMetaRegistry = entityMetaRegistry;
        this.contextCacheMap = contextCacheMap;
    }

    public static PersistenceCacheContext of(EntityMetaRegistry entityMetaRegistry) {
        return new PersistenceCacheContext(entityMetaRegistry, new HashMap<>());
    }

    public Optional<Object> tryGetEntityCache(Class<?> entityClazz, Object id) {
        final EntityClassMappingMeta entityClassMappingMeta = entityMetaRegistry.getEntityMeta(entityClazz);
        final EntityIdentifier identifier = EntityIdentifier.fromIdColumnMetaWithValue(entityClassMappingMeta.getIdColumnMeta(), id);
        return Optional.ofNullable(contextCacheMap.get(identifier));
    }

    public void putEntityCache(Object entity) {
        final EntityClassMappingMeta entityClassMappingMeta = entityMetaRegistry.getEntityMeta(entity.getClass());
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, entityClassMappingMeta);
        EntityIdentifier identifier = objectMappingMeta.getEntityIdentifier();
        contextCacheMap.put(identifier, entity);
    }

    public void purgeEntityCache(Object entity) {
        final EntityClassMappingMeta entityClassMappingMeta = entityMetaRegistry.getEntityMeta(entity.getClass());
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, entityClassMappingMeta);
        EntityIdentifier identifier = objectMappingMeta.getEntityIdentifier();

        contextCacheMap.remove(identifier);
    }

    public void clear() {
        contextCacheMap.clear();
    }
}
