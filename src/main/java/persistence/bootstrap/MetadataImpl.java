package persistence.bootstrap;

import persistence.entity.database.CollectionLoader;
import persistence.entity.database.EntityLoader;
import persistence.entity.database.EntityPersister;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO: 바인더?
// org.hibernate.boot.model.internal 참조

public class MetadataImpl {
    private final Map<Class<?>, EntityPersister<?>> entityPersisterMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, EntityLoader<?>> entityLoaderMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, CollectionLoader<?>> collectionLoaderMap = new ConcurrentHashMap<>();
//    private final Map<Class<?>, CollectionPersister> collectionPersisterMap = new ConcurrentHashMap<>();

    public <T> void register(Class<T> clazz, EntityPersister<T> entityPersister, EntityLoader<T> entityLoader,
                             CollectionLoader<T> collectionLoader) {
        entityPersisterMap.put(clazz, entityPersister);
        entityLoaderMap.put(clazz, entityLoader);
        collectionLoaderMap.put(clazz, collectionLoader);
    }

    public <T> CollectionLoader<T> getCollectionLoaderByClass(Class<T> clazz) {
        //noinspection unchecked
        return (CollectionLoader<T>) collectionLoaderMap.get(clazz);
    }

    public <T> EntityLoader<T> getEntityLoaderByClass(Class<T> clazz) {
        //noinspection unchecked
        return (EntityLoader<T>) entityLoaderMap.get(clazz);
    }

    public <T> EntityPersister<T> getEntityPersisterByClass(Class<T> clazz) {
        //noinspection unchecked
        return (EntityPersister<T>) entityPersisterMap.get(clazz);
    }
}
