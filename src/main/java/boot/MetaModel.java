package boot;

import persistence.proxy.ProxyFactory;
import persistence.sql.common.util.CamelToSnakeConverter;
import persistence.sql.context.EntityPersister;
import persistence.sql.context.impl.DefaultEntityPersister;
import persistence.sql.dml.Database;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.loader.EntityLoader;

import java.util.HashMap;
import java.util.Map;

public class MetaModel {
    private final Map<Class<?>, EntityPersister> entityPersisterMap = new HashMap<>();
    private final Map<Class<?>, EntityLoader<?>> entityLoaderMap = new HashMap<>();

    public void init(Metadata metadata, ProxyFactory proxyFactory) {
        Map<Class<?>, MetadataLoader<?>> entityBindingMap = metadata.getEntityBinding();
        Database database = metadata.database();

        for (Map.Entry<Class<?>, MetadataLoader<?>> entry : entityBindingMap.entrySet()) {
            Class<?> clazz = entry.getKey();
            MetadataLoader<?> metadataLoader = entry.getValue();
            entityPersisterMap.put(clazz,
                    new DefaultEntityPersister(database, CamelToSnakeConverter.getInstance(), metadataLoader));
            entityLoaderMap.put(clazz,
                    new EntityLoader<>(database, metadataLoader, CamelToSnakeConverter.getInstance(), proxyFactory));
        }
    }

    public EntityPersister entityPersister(Class<?> entityClass) {
        if (entityClass == null || !entityPersisterMap.containsKey(entityClass)) {
            throw new IllegalArgumentException("Not Found EntityPersister");
        }

        return entityPersisterMap.get(entityClass);
    }

    @SuppressWarnings("unchecked")
    public <T> EntityLoader<T> entityLoader(Class<T> entityClass) {
        if (entityClass == null || !entityLoaderMap.containsKey(entityClass)) {
            throw new IllegalArgumentException("Not Found EntityLoader");
        }

        return (EntityLoader<T>) entityLoaderMap.get(entityClass);
    }
}
