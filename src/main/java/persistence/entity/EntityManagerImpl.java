package persistence.entity;

import database.sql.dml.part.ValueMap;
import persistence.bootstrap.MetadataImpl;
import persistence.entity.context.PersistenceContext;
import persistence.entity.context.PersistenceContextImpl;
import persistence.entity.context.PersistentClass;
import persistence.entity.data.EntitySnapshot;
import persistence.entity.database.CollectionLoader;
import persistence.entity.database.EntityLoader;
import persistence.entity.database.EntityPersister;

import java.util.Objects;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final MetadataImpl metadata;

    private EntityManagerImpl(PersistenceContext persistenceContext, MetadataImpl metadata) {
        this.persistenceContext = persistenceContext;
        this.metadata = metadata;
    }

    public static EntityManagerImpl from(MetadataImpl metadata) {
        return new EntityManagerImpl(new PersistenceContextImpl(), metadata);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        PersistentClass<T> persistentClass = PersistentClass.from(clazz, metadata);

        Object cached = persistenceContext.getEntity(persistentClass, id);
        if (Objects.isNull(cached)) {
            loadEntity(persistentClass, id);
        }
        return (T) persistenceContext.getEntity(persistentClass, id);
    }

    private <T> void loadEntity(PersistentClass<T> persistentClass, Long id) {
        if (persistentClass.hasAssociation()) {
            CollectionLoader<T> collectionLoader = persistentClass.getCollectionLoader();
            collectionLoader.load(id).ifPresent(persistenceContext::addEntity);
        } else {
            persistentClass.getEntityLoader().load(id).ifPresent(persistenceContext::addEntity);
        }
    }

    @Override
    public <T> T persist(Object object) {
        PersistentClass<T> persistentClass = (PersistentClass<T>) PersistentClass.from(object.getClass(), metadata);

        if (isInsertOperation(persistentClass, object)) {
            return insertEntity(persistentClass, object);
        }
        return updateEntity(persistentClass, (T) object);
    }

    /**
     * id 가 없으면 insert, id 가 있으면 insert 일수도 아닐 수도 있다.
     * 비즈니스 로직에서 잘 넣어줬을 거라고 생각하고, 현재 퍼스트레벨 캐시에 있는지만 확인한다.
     */
    private <T> boolean isInsertOperation(PersistentClass<T> persistentClass, Object entity) {
        Long id = persistentClass.getRowId(entity);

        return id == null || persistenceContext.getEntity(persistentClass, id) == null;
    }

    private <T> T insertEntity(PersistentClass<T> persistentClass, Object object) {
        EntityPersister<T> entityPersister = persistentClass.getEntityPersister();
        EntityLoader<T> entityLoader = persistentClass.getEntityLoader();

        Long newId = entityPersister.insert(object);
        T load = entityLoader.load(newId).get();

        // TODO: lazy/eager 분리할 때 여길 깔끔하게 할 수 있을까?
        if (!persistentClass.hasAssociation()) {
            persistenceContext.addEntity(load);
        }
        return load;
    }

    private <T> T updateEntity(PersistentClass<T> persistentClass, T entity) {
        Long id = persistentClass.getRowId(entity);
        Object oldEntity = find(persistentClass.getMappedClass(), id);
        ValueMap diff = EntitySnapshot.of(oldEntity).diff(EntitySnapshot.of(entity));

        if (!diff.isEmpty()) {
            EntityPersister<T> entityPersister = persistentClass.getEntityPersister();
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
        PersistentClass<?> persistentClass = PersistentClass.from(entity.getClass(), metadata);
        EntityPersister<?> entityPersister = persistentClass.getEntityPersister();

        Long id = persistentClass.getRowId(entity);
        entityPersister.delete(id);

        persistenceContext.removeEntity(entity);
    }

}
