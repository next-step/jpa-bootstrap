package persistence.bootstrap;

import persistence.entity.database.CollectionLoader2;
import persistence.entity.database.EntityLoader2;
import persistence.entity.database.EntityPersister2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO: 바인더?
public class MetadataImpl {
    private final Map<Class<?>, EntityPersister2<?>> entityPersisterMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, EntityLoader2<?>> entityLoaderMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, CollectionLoader2<?>> collectionLoaderMap = new ConcurrentHashMap<>();
//    private final Map<Class<?>, CollectionPersister> collectionPersisterMap = new ConcurrentHashMap<>();

    public <T> void register(Class<T> clazz, EntityPersister2<T> entityPersister, EntityLoader2<T> entityLoader, CollectionLoader2<T> collectionLoader) {
        entityPersisterMap.put(clazz, entityPersister);
        entityLoaderMap.put(clazz, entityLoader);
        collectionLoaderMap.put(clazz, collectionLoader);
    }
}
