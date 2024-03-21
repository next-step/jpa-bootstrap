package persistence.bootstrap;

import persistence.entity.database.CollectionLoader2;
import persistence.entity.database.EntityLoader2;
import persistence.entity.database.EntityPersister2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO: 바인더?
// org.hibernate.boot.model.internal 참조

public class MetadataImpl {
    private final static MetadataImpl INSTANCE = new MetadataImpl();

    private final Map<Class<?>, EntityPersister2<?>> entityPersisterMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, EntityLoader2<?>> entityLoaderMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, CollectionLoader2<?>> collectionLoaderMap = new ConcurrentHashMap<>();
//    private final Map<Class<?>, CollectionPersister> collectionPersisterMap = new ConcurrentHashMap<>();

    public <T> void register(Class<T> clazz, EntityPersister2<T> entityPersister, EntityLoader2<T> entityLoader,
                             CollectionLoader2<T> collectionLoader) {
        INSTANCE.entityPersisterMap.put(clazz, entityPersister);
        INSTANCE.entityLoaderMap.put(clazz, entityLoader);
        INSTANCE.collectionLoaderMap.put(clazz, collectionLoader);
    }

    public static <T> CollectionLoader2<T> getCollectionLoaderByClass(Class<T> clazz) {
        //noinspection unchecked
        return (CollectionLoader2<T>) INSTANCE.collectionLoaderMap.get(clazz);
    }

    public static <T> EntityLoader2<T> getEntityLoaderByClass(Class<T> clazz) {
        //noinspection unchecked
        return (EntityLoader2<T>) INSTANCE.entityLoaderMap.get(clazz);
    }

    public static <T> EntityPersister2<T> getEntityPersisterByClass(Class<T> clazz) {
        //noinspection unchecked
        return (EntityPersister2<T>) INSTANCE.entityPersisterMap.get(clazz);
    }
}
