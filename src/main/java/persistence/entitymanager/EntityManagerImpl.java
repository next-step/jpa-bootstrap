package persistence.entitymanager;

import database.sql.dml.part.ValueMap;
import persistence.bootstrap.Metadata;
import persistence.bootstrap.Metamodel;
import persistence.entity.context.*;
import persistence.entity.data.EntitySnapshot;
import persistence.entity.database.CollectionLoader;
import persistence.entity.database.EntityLoader;
import persistence.entity.database.EntityPersister;

import java.util.Objects;

public class EntityManagerImpl extends AbstractEntityManager {
    private final Metadata metadata;
    private final PersistenceContext persistenceContext;

    public static EntityManager newEntityManager(Metamodel metamodel, Metadata metadata) {
        return new EntityManagerImpl(metadata, metamodel);
    }

    private EntityManagerImpl(Metadata metadata, Metamodel metamodel) {
        super(metamodel);

        this.metadata = metadata;
        this.persistenceContext = new PersistenceContextImpl(metadata, new FirstLevelCache(), new EntityEntries());
    }

    @Override
    public <T> T find(Class<T> entityClass, Long id) {
        PersistentClass<T> persistentClass = metadata.getPersistentClass(entityClass);

        Object cached = persistenceContext.getEntity(persistentClass, id);
        if (Objects.isNull(cached)) {
            loadEntity(persistentClass, id);
        }
        return (T) persistenceContext.getEntity(persistentClass, id);
    }

    private <T> void loadEntity(PersistentClass<T> persistentClass, Long id) {
        if (persistentClass.hasAssociation()) {
            CollectionLoader<T> collectionLoader = getCollectionLoader(persistentClass);
            collectionLoader.load(id).ifPresent(persistenceContext::addEntity);
        } else {
            getEntityLoader(persistentClass).load(id).ifPresent(persistenceContext::addEntity);
        }
    }

    @Override
    public <T> T persist(Object entity) {
        PersistentClass<T> persistentClass = (PersistentClass<T>) metadata.getPersistentClass(entity.getClass());

        if (isInsertOperation(persistentClass, entity)) {
            return insertEntity(persistentClass, entity);
        }
        return updateEntity(persistentClass, (T) entity);
    }

    /**
     * id 가 없으면 insert, id 가 있으면 insert 일수도 아닐 수도 있다.
     * 비즈니스 로직에서 잘 넣어줬을 거라고 생각하고, 현재 퍼스트레벨 캐시에 있는지만 확인한다.
     */
    private <T> boolean isInsertOperation(PersistentClass<T> persistentClass, Object entity) {
        Long id = metadata.getRowId(entity);

        return id == null || persistenceContext.getEntity(persistentClass, id) == null;
    }

    private <T> T insertEntity(PersistentClass<T> persistentClass, Object object) {
        EntityPersister<T> entityPersister = getEntityPersister(persistentClass);
        EntityLoader<T> entityLoader = getEntityLoader(persistentClass);

        Long newId = entityPersister.insert(object);
        T load = entityLoader.load(newId).get();

        // TODO: lazy/eager 분리할 때 여길 깔끔하게 할 수 있을까?
        if (!persistentClass.hasAssociation()) {
            persistenceContext.addEntity(load);
        }
        return load;
    }

    private <T> T updateEntity(PersistentClass<T> persistentClass, T entity) {
        Long id = metadata.getRowId(entity);
        T oldEntity = find(persistentClass.getMappedClass(), id);
        ValueMap diff = EntitySnapshot.of(persistentClass, oldEntity).diff(EntitySnapshot.of(persistentClass, entity));

        if (!diff.isEmpty()) {
            EntityPersister<T> entityPersister = getEntityPersister(persistentClass);
            entityPersister.update(id, diff);
            persistenceContext.addEntity(entity);
        }
        return entity;
    }

    @Override
    public void remove(Object entity) {
        if (persistenceContext.isRemoved(entity)) {
            // 아무것도 안함
            return;
        }
        PersistentClass<?> persistentClass = metadata.getPersistentClass(entity.getClass());
        Long id = metadata.getRowId(entity);

        EntityPersister<?> entityPersister = getEntityPersister(persistentClass);
        entityPersister.delete(id);

        persistenceContext.removeEntity(entity);
    }

    @Override
    public <T> void createTable(Class<T> clazz) {
        EntityPersister<T> entityPersister = getEntityPersister(clazz);
        entityPersister.createTable();
    }

    @Override
    public <T> void dropTable(Class<T> clazz, boolean ifExists) {
        EntityPersister<T> entityPersister = getEntityPersister(clazz);
        entityPersister.dropTable(ifExists);
    }
}
