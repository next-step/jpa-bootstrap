package persistence.bootstrap;

import persistence.entity.context.PersistentClass;
import persistence.entity.database.CollectionLoader;
import persistence.entity.database.EntityLoader;
import persistence.entity.database.EntityPersister;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO: 바인더?
// org.hibernate.boot.model.internal 참조

public class MetadataImpl {
    public static MetadataImpl INSTANCE = new MetadataImpl();

    private final Map<Class<?>, EntityPersister<?>> entityPersisterMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, EntityLoader<?>> entityLoaderMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, CollectionLoader<?>> collectionLoaderMap = new ConcurrentHashMap<>();
    private List<Class<?>> components = null;
//    private final Map<Class<?>, CollectionPersister> collectionPersisterMap = new ConcurrentHashMap<>();

    private MetadataImpl() {
    }

    public <T> void register(PersistentClass<T> persistentClass,
                             EntityPersister<T> entityPersister,
                             EntityLoader<T> entityLoader,
                             CollectionLoader<T> collectionLoader) {
        Class<?> clazz = persistentClass.getMappedClass();

        entityPersisterMap.put(clazz, entityPersister);
        entityLoaderMap.put(clazz, entityLoader);
        collectionLoaderMap.put(clazz, collectionLoader);
    }

    public <T> PersistentClass<T> getPersistentClass(Class<T> clazz) {
        return PersistentClass.from(clazz, this);
    }

    public <T> EntityLoader<T> getEntityLoader(PersistentClass<T> persistentClass) {
        Class<T> mappedClass = persistentClass.getMappedClass();
        return getEntityLoader(mappedClass);
    }

    private <T> EntityLoader<T> getEntityLoader(Class<T> mappedClass) {
        //noinspection unchecked
        return (EntityLoader<T>) entityLoaderMap.get(mappedClass);
    }

    public <T> EntityPersister<T> getEntityPersister(PersistentClass<T> persistentClass) {
        Class<T> mappedClass = persistentClass.getMappedClass();
        return getEntityPersister(mappedClass);
    }

    private <T> EntityPersister<T> getEntityPersister(Class<T> mappedClass) {
        //noinspection unchecked
        return (EntityPersister<T>) entityPersisterMap.get(mappedClass);
    }

    public <T> CollectionLoader<T> getCollectionLoader(PersistentClass<T> persistentClass) {
        Class<T> mappedClass = persistentClass.getMappedClass();
        return getCollectionLoader(mappedClass);
    }

    private <T> CollectionLoader<T> getCollectionLoader(Class<T> mappedClass) {
        //noinspection unchecked
        return (CollectionLoader<T>) collectionLoaderMap.get(mappedClass);
    }

    public void setComponents(List<Class<?>> components) {
        this.components = components;

    }

    public List<Class<?>> getComponents() {
        return components;
    }
}
