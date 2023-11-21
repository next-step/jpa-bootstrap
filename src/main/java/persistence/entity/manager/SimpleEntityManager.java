package persistence.entity.manager;

import persistence.action.ActionQueue;
import persistence.context.EntityKey;
import persistence.context.EntityKeyGenerator;
import persistence.context.PersistenceContext;
import persistence.context.SimplePersistenceContext;
import persistence.core.EntityMetadata;
import persistence.core.MetaModel;
import persistence.entity.entry.Status;
import persistence.entity.proxy.EntityProxyFactory;
import persistence.event.*;
import persistence.exception.PersistenceException;
import persistence.util.ReflectionUtils;

import java.util.Objects;

public class SimpleEntityManager implements EntityManager {

    private final MetaModel metaModel;
    private final SessionCloseStrategy sessionCloseStrategy;
    private final EntityProxyFactory entityProxyFactory;
    private final EntityKeyGenerator entityKeyGenerator;
    private final PersistenceContext persistenceContext;

    public SimpleEntityManager(final MetaModel metaModel, final SessionCloseStrategy sessionCloseStrategy) {
        this.metaModel = metaModel;
        this.sessionCloseStrategy = sessionCloseStrategy;
        this.entityProxyFactory = metaModel.getEntityProxyFactory();
        this.entityKeyGenerator = new EntityKeyGenerator(metaModel);
        this.persistenceContext = new SimplePersistenceContext();
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object id) {
        final EntityKey entityKey = entityKeyGenerator.generate(clazz, id);
        final Object entity = persistenceContext.getEntity(entityKey)
                .orElseGet(() -> initEntity(metaModel.getEntityMetadata(clazz), entityKey));
        return clazz.cast(entity);
    }

    @Override
    public void persist(final Object entity) {
        final Object idValue = extractId(entity);
        if (Objects.nonNull(idValue)) {
            checkEntityAlreadyExists(entity, idValue);
            processUpdate(entity);
            return;
        }

        processInsert(entity);
    }

    @Override
    public <T> T merge(final T entity) {
        final Object idValue = extractId(entity);
        final boolean isIdValueNull = Objects.isNull(idValue);
        if (isIdValueNull) {
            throw new PersistenceException("Id value 없이 merge 할 수 없습니다.");
        }

        processUpdate(entity);
        return entity;
    }

    @Override
    public void remove(final Object entity) {
        final Object idValue = extractId(entity);

        final EntityKey entityKey = entityKeyGenerator.generate(entity.getClass(), idValue);
        persistenceContext.removeEntity(entityKey);

        persistenceContext.updateEntityEntryStatus(entity, Status.DELETED);

        metaModel.getEventListenerGroup(EventType.DELETE)
                .fireEvent(new DeleteEvent(entity), DeleteEventListener::onDelete);
    }

    @Override
    public void flush() {
        final ActionQueue actionQueue = metaModel.getActionQueue();
        actionQueue.flush();
    }

    @Override
    public void close() {
        flush();
        this.sessionCloseStrategy.close();
    }

    private Object extractId(final Object entity) {
        return ReflectionUtils.getFieldValue(entity, metaModel.getEntityMetadata(entity.getClass()).getIdColumnFieldName());
    }

    private <T> Object initEntity(final EntityMetadata<T> entityMetadata, final EntityKey entityKey) {
        final Object key = entityKey.getKey();
        final Object entityFromDatabase =
                metaModel.getEventListenerGroup(EventType.LOAD)
                        .fireEventReturn(new LoadEvent<>(key, entityMetadata.getType()), LoadEventListener::onLoad);
        if(Objects.isNull(entityFromDatabase)) {
            return null;
        }

        entityMetadata.getLazyManyToOneColumns()
                .forEach(manyToOneColumn -> entityProxyFactory.initManyToOneProxy(entityFromDatabase, manyToOneColumn));

        entityMetadata.getLazyOneToManyColumns()
                .forEach(oneToManyColumn -> entityProxyFactory.initOneToManyProxy(key, entityFromDatabase, oneToManyColumn));

        persistenceContext.addEntityEntry(entityFromDatabase, Status.LOADING);
        persistenceContext.addEntity(entityKey, entityFromDatabase);
        persistenceContext.getDatabaseSnapshot(entityKey, entityFromDatabase);

        return entityFromDatabase;
    }

    private void checkEntityAlreadyExists(final Object entity, final Object idValue) {
        final EntityKey entityKey = entityKeyGenerator.generate(entity.getClass(), idValue);
        if (persistenceContext.hasEntity(entityKey)) {
            throw new PersistenceException("이미 persistence context에 존재하는 entity 는 persist 할 수 없습니다.");
        }
    }

    private void processInsert(final Object entity) {
        persistenceContext.addEntityEntry(entity, Status.SAVING);

        metaModel.getEventListenerGroup(EventType.PERSIST).fireEvent(new PersistEvent(entity), PersistEventListener::onPersist);

        final Object idValue = extractId(entity);
        final EntityKey entityKey = entityKeyGenerator.generate(entity.getClass(), idValue);
        persistenceContext.addEntity(entityKey, entity);
    }

    private void processUpdate(final Object entity) {
        final Object idValue = extractId(entity);
        final EntityKey entityKey = entityKeyGenerator.generate(entity.getClass(), idValue);

        Object foundEntity = null;
        if (!persistenceContext.hasEntity(entityKey)) {
            foundEntity = find(entity.getClass(), idValue);
        }

        if (isDirty(entity)) {
            updateEntityEntry(foundEntity, entity);
            metaModel.getEventListenerGroup(EventType.MERGE).fireEvent(new MergeEvent(entity), MergeEventListener::onMerge);
            persistenceContext.addEntity(entityKey, entity);
        }
    }

    private void updateEntityEntry(final Object foundEntity, final Object entity) {
        if (Objects.nonNull(foundEntity)) {
            persistenceContext.updateEntityEntryStatus(foundEntity, Status.GONE);
            persistenceContext.addEntityEntry(entity, Status.SAVING);
        }
    }

    private boolean isDirty(final Object entity) {
        final Object idValue = extractId(entity);
        final EntityKey entityKey = entityKeyGenerator.generate(entity.getClass(), idValue);
        final Object databaseSnapshot = persistenceContext.getDatabaseSnapshot(entityKey, entity);

        return metaModel.getEntityMetadata(entity.getClass())
                .getColumnFieldNames()
                .stream()
                .anyMatch(columnName -> hasDifferentValue(entity, databaseSnapshot, columnName));
    }

    private boolean hasDifferentValue(final Object entity, final Object cachedDatabaseSnapshot, final String columnName) {
        final Object targetValue = ReflectionUtils.getFieldValue(entity, columnName);
        final Object snapshotValue = ReflectionUtils.getFieldValue(cachedDatabaseSnapshot, columnName);
        return !Objects.equals(targetValue, snapshotValue);
    }

}
