package persistence.context;

import persistence.core.EntityMetadataProvider;

import java.util.HashMap;
import java.util.Map;

public class EntityKeyGenerator {

    private final Map<Class<?>, Map<Object, EntityKey>> cache;
    private final EntityMetadataProvider entityMetadataProvider;

    public EntityKeyGenerator(final EntityMetadataProvider entityMetadataProvider) {
        this.entityMetadataProvider = entityMetadataProvider;
        this.cache = new HashMap<>();
    }

    public EntityKey generate(final Class<?> entityClass, final Object key) {
        return cache.computeIfAbsent(entityClass, this::createKeyCacheForClass)
                .computeIfAbsent(key, object -> EntityKey.of(entityMetadataProvider.getEntityMetadata(entityClass), key));
    }

    private Map<Object, EntityKey> createKeyCacheForClass(final Class<?> entityClass) {
        return new HashMap<>();
    }

}
