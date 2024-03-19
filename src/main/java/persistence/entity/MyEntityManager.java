package persistence.entity;

import boot.metamodel.MetaModel;
import persistence.persistencecontext.EntitySnapshot;
import persistence.persistencecontext.MyPersistenceContext;
import persistence.persistencecontext.PersistenceContext;

import java.util.List;

public class MyEntityManager implements EntityManager {

    private final MetaModel metaModel;
    private final PersistenceContext persistenceContext;

    public MyEntityManager(MetaModel metaModel) {
        this.metaModel = metaModel;
        this.persistenceContext = new MyPersistenceContext();
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return (T) persistenceContext.getEntity(clazz, id)
                .orElseGet(() -> {
                    EntityLoader<T> entityLoader = metaModel.getEntityLoader(clazz);
                    T foundEntity = entityLoader.find(id);
                    EntityMeta<T> entityMeta = metaModel.getEntityMetaFrom(foundEntity);
                    addToCache(entityMeta.extractId(foundEntity), foundEntity);
                    return foundEntity;
                });
    }

    @Override
    public <T> T persist(T entity) {
        persistenceContext.addEntityEntry(entity, EntityEntryStatus.SAVING);
        EntityPersister<?> entityPersister = metaModel.getEntityPersister(entity.getClass());
        Object generatedId = entityPersister.insert(entity);
        EntityMeta<T> entityMeta = metaModel.getEntityMetaFrom(entity);
        entityMeta.injectId(entity, generatedId);
        addToCache(entityMeta.extractId(entity), entity);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        EntityMeta<?> entityMeta = metaModel.getEntityMetaFrom(entity.getClass());
        persistenceContext.removeEntity(entityMeta.extractId(entity), entity);
        EntityPersister<?> entityPersister = metaModel.getEntityPersister(entity.getClass());
        entityPersister.delete(entity);
    }

    @Override
    public <T> T merge(T entity) {
        EntityMeta<T> entityMeta = metaModel.getEntityMetaFrom(entity);
        Object id = entityMeta.extractId(entity);
        EntitySnapshot snapshot = (EntitySnapshot) persistenceContext.getCachedDatabaseSnapshot(id, entity);
        if (snapshot.isChanged(entity)) {
            persistenceContext.addEntity(id, entity);
        }
        return entity;
    }

    @Override
    public void flush() {
        List<Object> entities = persistenceContext.getDirtyEntities();
        for (Object entity : entities) {
            EntityPersister<?> entityPersister = metaModel.getEntityPersister(entity.getClass());
            entityPersister.update(entity);
            persistenceContext.addEntityEntry(entity, EntityEntryStatus.GONE);
        }
    }

    @Override
    public <T> EntityMeta<T> getEntityMetaFrom(T entity) {
        return metaModel.getEntityMetaFrom(entity);
    }

    private void addToCache(Object id, Object entity) {
        persistenceContext.addEntity(id, entity);
        persistenceContext.getDatabaseSnapshot(id, entity);
    }
}
