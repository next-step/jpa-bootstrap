package registry.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import persistence.sql.schema.meta.EntityClassMappingMeta;

public class MetaModelContext {

    private final Map<Class<?>, EntityClassMappingMeta> entityClassMappingMetaMap = new ConcurrentHashMap<>();

    public MetaModelContext() {
    }

    public EntityClassMappingMeta getEntityMeta(Class<?> clazz) {
        return entityClassMappingMetaMap.get(clazz);
    }

    public void putEntityMeta(Class<?> clazz, EntityClassMappingMeta entityClassMappingMeta) {
        entityClassMappingMetaMap.put(clazz, entityClassMappingMeta);
    }
}
