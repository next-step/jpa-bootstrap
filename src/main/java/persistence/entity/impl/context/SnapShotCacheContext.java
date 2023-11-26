package persistence.entity.impl.context;

import java.util.HashMap;
import java.util.Map;
import persistence.entity.impl.EntityIdentifier;
import persistence.entity.impl.SnapShot;
import persistence.sql.schema.meta.EntityClassMappingMeta;
import persistence.sql.schema.meta.EntityObjectMappingMeta;
import registry.EntityMetaRegistry;

public class SnapShotCacheContext {

    private final Map<EntityIdentifier, Object> contextSnapshotCacheMap;
    private final EntityMetaRegistry entityMetaRegistry;

    private SnapShotCacheContext(EntityMetaRegistry entityMetaRegistry, Map<EntityIdentifier, Object> contextSnapshotCacheMap) {
        this.entityMetaRegistry = entityMetaRegistry;
        this.contextSnapshotCacheMap = contextSnapshotCacheMap;
    }

    public static SnapShotCacheContext of(EntityMetaRegistry entityMetaRegistry) {
        return new SnapShotCacheContext(entityMetaRegistry, new HashMap<>());
    }

    public Object putSnapShotCache(Object id, Object entity) {
        final EntityClassMappingMeta classMappingMeta = entityMetaRegistry.getEntityMeta(entity.getClass());

        final EntityIdentifier identifier = EntityIdentifier.fromIdColumnMetaWithValue(classMappingMeta.getIdColumnMeta(), id);
        return contextSnapshotCacheMap.put(identifier, entity);
    }

    public SnapShot getSnapShotCache(Class<?> clazz, Object id) {
        final EntityClassMappingMeta entityClassMappingMeta = entityMetaRegistry.getEntityMeta(clazz);

        final EntityIdentifier identifier = EntityIdentifier.fromIdColumnMetaWithValue(entityClassMappingMeta.getIdColumnMeta(), id);
        return new SnapShot(contextSnapshotCacheMap.get(identifier), entityClassMappingMeta);
    }

    public void purgeSnapShotCache(Object entity) {
        final EntityClassMappingMeta entityClassMappingMeta = entityMetaRegistry.getEntityMeta(entity.getClass());

        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, entityClassMappingMeta);
        EntityIdentifier identifier = objectMappingMeta.getEntityIdentifier();

        contextSnapshotCacheMap.remove(identifier);
    }

    public void clear() {
        contextSnapshotCacheMap.clear();
    }


}
