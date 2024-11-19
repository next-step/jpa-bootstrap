package persistence;

import boot.Metamodel;
import builder.dml.DMLColumnData;
import builder.dml.EntityData;
import builder.dml.EntityMetaData;
import builder.dml.EntityObjectData;
import event.EventListenerGroup;
import event.EventListenerRegistry;
import event.EventType;
import event.action.ActionQueue;
import event.listener.delete.DeleteEventListener;
import event.listener.load.LoadEventListener;
import event.listener.merge.MergeEventListener;
import event.listener.persist.PersistEventListener;

import java.util.List;
import java.util.function.BiFunction;

public class EntityManagerImpl implements EntityManager {

    private final PersistenceContext persistenceContext;
    private final EventListenerRegistry eventListenerRegistry;
    private final Metamodel metamodel;
    private final ActionQueue actionQueue;

    public EntityManagerImpl(
            PersistenceContext persistenceContext,
            Metamodel metamodel,
            EventListenerRegistry eventListenerRegistry,
            ActionQueue actionQueue
    ) {
        this.persistenceContext = persistenceContext;
        this.metamodel = metamodel;
        this.eventListenerRegistry = eventListenerRegistry;
        this.actionQueue = actionQueue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T find(Class<T> clazz, Object id) {
        EntityKey entityKey = new EntityKey(id, clazz);
        EntityEntry entityEntry = this.persistenceContext.getEntityEntryMap(entityKey);

        if (entityEntry != null && entityEntry.checkEntityStatus(EntityStatus.MANAGED)) {
            EntityData persistEntityData = this.persistenceContext.findEntity(entityKey);
            return clazz.cast(persistEntityData.getEntityInstance());
        }

        EntityData entityData = createEntityData(clazz, id);

        BiFunction<LoadEventListener<T>, EntityData, T> function = LoadEventListener::onLoad;

        EventListenerGroup<LoadEventListener<T>> eventListenerGroup = (EventListenerGroup<LoadEventListener<T>>) this.eventListenerRegistry.getEventListenerGroup(EventType.LOAD);

        T findObject = eventListenerGroup.handleEventWithReturn(entityData, function);

        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.LOADING);
        insertPersistenceContext(entityKey, entityData);
        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.MANAGED);

        return findObject;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void persist(Object entityInstance) {
        EntityData entityData = createEntityData(entityInstance);
        EntityKey entityKey = new EntityKey(entityData);

        EntityEntry entityEntry = this.persistenceContext.getEntityEntryMap(entityKey);

        if (entityEntry != null && !entityEntry.checkEntityStatus(EntityStatus.MANAGED)) {
            return;
        }

        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.SAVING);

        EventListenerGroup<PersistEventListener> eventListenerGroup = (EventListenerGroup<PersistEventListener>) this.eventListenerRegistry.getEventListenerGroup(EventType.PERSIST);

        eventListenerGroup.handleEvent(entityData, PersistEventListener::onPersist);

        insertPersistenceContext(entityKey, entityData);
        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.MANAGED);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void merge(Object entityInstance) {
        EntityData entityData = createEntityData(entityInstance);

        EntityKey entityKey = new EntityKey(entityData);

        EntityEntry entityEntry = this.persistenceContext.getEntityEntryMap(entityKey);

        if (entityEntry != null && !entityEntry.checkEntityStatus(EntityStatus.MANAGED)) {
            return;
        }

        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.SAVING);

        EntityData diffBuilderData = checkDirtyCheck(entityData);
        if (diffBuilderData.getEntityColumn().getColumns().isEmpty()) {
            return;
        }

        EventListenerGroup<MergeEventListener> eventListenerGroup = (EventListenerGroup<MergeEventListener>) this.eventListenerRegistry.getEventListenerGroup(EventType.MERGE);

        eventListenerGroup.handleEvent(entityData, MergeEventListener::onMerge);

        insertPersistenceContext(entityKey, entityData);
        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.MANAGED);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void remove(Object entityInstance) {
        EntityMetaData entityMetaData = new EntityMetaData(entityInstance.getClass());
        EntityObjectData entityObjectData = new EntityObjectData(entityInstance);

        EntityData entityData = new EntityData(entityMetaData, entityObjectData);
        EntityKey entityKey = new EntityKey(entityData);

        EntityEntry entityEntry = this.persistenceContext.getEntityEntryMap(entityKey);

        if (entityEntry != null && entityEntry.checkEntityStatus(EntityStatus.GONE)) {
            return;
        }

        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.DELETED);

        EventListenerGroup<DeleteEventListener> eventListenerGroup = (EventListenerGroup<DeleteEventListener>) this.eventListenerRegistry.getEventListenerGroup(EventType.DELETE);

        eventListenerGroup.handleEvent(entityData, DeleteEventListener::onDelete);

        this.persistenceContext.deleteEntity(entityKey);
        this.persistenceContext.deleteDatabaseSnapshot(entityKey);
        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.GONE);
    }

    @Override
    public void flush() {
        this.actionQueue.execute();
    }

    @Override
    public void clear() {
        this.persistenceContext.clear();
        this.actionQueue.execute();
    }

    private EntityData checkDirtyCheck(EntityData entityBuilderData) {
        EntityKey entityKey = new EntityKey(entityBuilderData);

        EntityData snapshotEntityData = this.persistenceContext.getDatabaseSnapshot(entityKey);

        List<DMLColumnData> differentColumns = entityBuilderData.getDifferentColumns(snapshotEntityData);

        return entityBuilderData.changeColumns(differentColumns);
    }

    private void insertPersistenceContext(EntityKey entityKey, EntityData EntityData) {
        this.persistenceContext.insertEntity(entityKey, EntityData);
        this.persistenceContext.insertDatabaseSnapshot(entityKey, EntityData);
    }

    private EntityData createEntityData(Class<?> clazz, Object id) {
        EntityMetaData entityMetaData = this.metamodel.entityMetaData(clazz);
        EntityObjectData entityObjectData = new EntityObjectData(clazz, id);

        return new EntityData(entityMetaData, entityObjectData);
    }

    private EntityData createEntityData(Object entityInstance) {
        EntityMetaData entityMetaData = this.metamodel.entityMetaData(entityInstance.getClass());
        EntityObjectData entityObjectData = new EntityObjectData(entityInstance);

        return new EntityData(entityMetaData, entityObjectData);
    }
}
