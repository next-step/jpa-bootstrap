package hibernate.entity;

import hibernate.entity.meta.column.EntityColumn;
import hibernate.entity.persistencecontext.EntityKey;
import hibernate.entity.persistencecontext.EntitySnapshot;
import hibernate.entity.persistencecontext.PersistenceContext;
import hibernate.metamodel.MetaModel;

import java.util.Map;

import static hibernate.entity.entityentry.Status.*;

public class EntityManagerImpl implements EntityManager {

    private final PersistenceContext persistenceContext;
    private final MetaModel metaModel;

    public EntityManagerImpl(
            final PersistenceContext persistenceContext,
            final MetaModel metaModel
    ) {
        this.persistenceContext = persistenceContext;
        this.metaModel = metaModel;
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object id) {
        EntityKey entityKey = new EntityKey(id, clazz);
        Object persistenceContextEntity = persistenceContext.getEntity(entityKey);
        if (persistenceContextEntity != null) {
            return (T) persistenceContextEntity;
        }

        T loadEntity = metaModel.getEntityLoader(clazz)
                .find(id);
        persistenceContext.addEntity(id, loadEntity, LOADING);
        return loadEntity;
    }

    @Override
    public void persist(final Object entity) {
        EntityPersister<?> entityPersister = metaModel.getEntityPersister(entity.getClass());
        EntityColumn entityId = metaModel.getEntityId(entity.getClass());
        Object id = entityId.getFieldValue(entity);
        if (id == null) {
            persistenceContext.addEntityEntry(entity, SAVING);
            Object generatedId = entityPersister.insert(entity);
            entityId.assignFieldValue(entity, generatedId);
            persistenceContext.addEntity(generatedId, entity);
            return;
        }

        if (persistenceContext.getEntity(new EntityKey(id, entity)) != null) {
            throw new IllegalStateException("이미 영속화되어있는 entity입니다.");
        }
        persistenceContext.addEntity(id, entity, SAVING);
        entityPersister.insert(entity);
    }

    @Override
    public void merge(final Object entity) {
        Object entityId = getNotNullEntityId(entity);
        Map<EntityColumn, Object> changedColumns = getSnapshot(entity, entityId).changedColumns(entity);
        if (changedColumns.isEmpty()) {
            return;
        }
        persistenceContext.addEntity(entityId, entity);
        metaModel.getEntityPersister(entity.getClass())
                .update(entityId, changedColumns);
    }

    private Object getNotNullEntityId(final Object entity) {
        Object entityId = metaModel.getEntityId(entity.getClass())
                .getFieldValue(entity);
        if (entityId == null) {
            throw new IllegalStateException("id가 없는 entity는 merge할 수 없습니다.");
        }
        return entityId;
    }

    private EntitySnapshot getSnapshot(final Object entity, final Object entityId) {
        EntityKey entityKey = new EntityKey(entityId, entity.getClass());
        EntitySnapshot snapshot = persistenceContext.getDatabaseSnapshot(entityKey);
        if (snapshot == null) {
            find(entity.getClass(), entityId);
            return persistenceContext.getDatabaseSnapshot(entityKey);
        }
        return snapshot;
    }

    @Override
    public void remove(final Object entity) {
        persistenceContext.addEntityEntry(entity, DELETED);
        metaModel.getEntityPersister(entity.getClass())
                .delete(entity);
        persistenceContext.removeEntity(entity);
    }
}
