package persistence.context;

import persistence.core.MetaModel;

import java.util.HashMap;
import java.util.Map;

public class EntityKeyGenerator {

    private final Map<Class<?>, Map<Object, EntityKey>> cache;
    private final MetaModel metaModel;

    public EntityKeyGenerator(final MetaModel metaModel) {
        this.metaModel = metaModel;
        this.cache = new HashMap<>();
    }

    public EntityKey generate(final Class<?> entityClass, final Object key) {
        return cache.computeIfAbsent(entityClass, this::createKeyCacheForClass)
                .computeIfAbsent(key, object -> EntityKey.of(metaModel.getEntityMetadata(entityClass), key));
    }

    private Map<Object, EntityKey> createKeyCacheForClass(final Class<?> entityClass) {
        return new HashMap<>();
    }

}
