package persistence.entity.context;

import database.sql.dml.part.ValueMap;
import persistence.bootstrap.Metadata;
import persistence.entity.data.EntitySnapshot;
import persistence.entity.database.CollectionLoader;
import persistence.entity.database.EntityPersister;
import persistence.entitymanager.SessionContract;

import java.util.Objects;
import java.util.Optional;

public class PersistenceContextImpl implements PersistenceContext {
    private final Metadata metadata;
    private final FirstLevelCache firstLevelCache;
    private final EntityEntries entityEntries;
    private final SessionContract sessionContract;

    public PersistenceContextImpl(Metadata metadata, FirstLevelCache firstLevelCache, EntityEntries entityEntries,
                                  SessionContract sessionContract) {
        this.metadata = metadata;
        this.firstLevelCache = firstLevelCache;
        this.entityEntries = entityEntries;
        this.sessionContract = sessionContract;
    }

    @Override
    public <T> T getEntity(PersistentClass<T> persistentClass, Long id) {
        Object cached = readFromCache(persistentClass, id);
        if (Objects.isNull(cached)) {
            readFromDatabase(persistentClass, id)
                    .ifPresent(this::writeToCache);
        }
        return readFromCache(persistentClass, id);
    }

    // handles database

    private <T> T readFromCache(PersistentClass<T> persistentClass, Long id) {
        EntityKey entityKey = metadata.entityKeyOf(persistentClass, id);

        if (!entityEntries.isReadable(entityKey)) return null;
        return (T) firstLevelCache.find(entityKey);
    }

    private <T> Optional<T> readFromDatabase(PersistentClass<T> persistentClass, Long id) {
        Optional<T> load;
        if (persistentClass.hasAssociation()) {
            CollectionLoader<T> collectionLoader = sessionContract.getCollectionLoader(persistentClass.getMappedClass());
            load = collectionLoader.load(id);
        } else {
            load = sessionContract.getEntityLoader(persistentClass.getMappedClass()).load(id);
        }
        return load;
    }

    private <T> void writeToCache(T entity) {
        EntityKey entityKey = metadata.entityKeyOfObject(entity);

        if (entityEntries.isAssignable(entityKey)) {
            firstLevelCache.store(entityKey, entity);
            entityEntries.managed(entityKey);
        }
    }

    @Override
    public boolean guessEntityIsNewOrNot(Object entity) {
        PersistentClass<?> persistentClass = metadata.getPersistentClass(entity.getClass());
        Long id = metadata.getRowId(entity);
        return id == null || readFromCache(persistentClass, id) == null;
    }

    @Override
    public <T> void insertEntity(T entity) {
        Class<T> aClass = (Class<T>) entity.getClass();
        PersistentClass<T> persistentClass = metadata.getPersistentClass(aClass);
        T inserted = insertEntityIntoDatabase(persistentClass, entity);
        writeToCache(inserted);
    }

    @Override
    public <T> void updateEntity(T entity) {
        Class<T> aClass = (Class<T>) entity.getClass();
        PersistentClass<T> persistentClass = metadata.getPersistentClass(aClass);
        T updated = updateEntityInDatabase(persistentClass, entity);
        writeToCache(updated);
    }

    private <T> T insertEntityIntoDatabase(PersistentClass<T> persistentClass, Object object) {
        Class<T> mappedClass = persistentClass.getMappedClass();
        Long newId = sessionContract.getEntityPersister(mappedClass).insert(object);
        return sessionContract.getEntityLoader(mappedClass).load(newId).orElseThrow();
    }

    private <T> T updateEntityInDatabase(PersistentClass<T> persistentClass, T entity) {
        Long id = metadata.getRowId(entity);
        EntitySnapshot oldSnapshot = EntitySnapshot.of(persistentClass, getEntity(persistentClass, id));
        EntitySnapshot newSnapshot = EntitySnapshot.of(persistentClass, entity);

        ValueMap diff = oldSnapshot.diff(newSnapshot);

        if (!diff.isEmpty()) {
            EntityPersister<T> entityPersister = sessionContract.getEntityPersister(persistentClass.getMappedClass());
            entityPersister.update(id, diff);
        }
        return entity;
    }

    @Override
    public void removeEntity(Object entity) {
        if (isRemovedInCache(entity)) {
            // 아무것도 안함
            return;
        }
        removeEntityFromDatabase(entity);
        removeEntityFromCache(entity);
    }

    private boolean isRemovedInCache(Object entity) {
        EntityKey entityKey = metadata.entityKeyOfObject(entity);
        return entityEntries.isRemoved(entityKey);
    }

    private void removeEntityFromDatabase(Object entity) {
        PersistentClass<?> persistentClass = metadata.getPersistentClass(entity.getClass());
        Long id = metadata.getRowId(entity);

        EntityPersister<?> entityPersister = sessionContract.getEntityPersister(persistentClass.getMappedClass());
        entityPersister.delete(id);
    }

    private void removeEntityFromCache(Object entity) {
        EntityKey entityKey = metadata.entityKeyOfObject(entity);

        if (entityEntries.isRemovable(entityKey)) {
            entityEntries.deleted(entityKey);
            firstLevelCache.delete(entityKey);
            entityEntries.gone(entityKey);
        }
    }
}
