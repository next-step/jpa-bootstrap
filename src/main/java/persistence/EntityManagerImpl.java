package persistence;

import boot.Metamodel;
import builder.dml.DMLColumnData;
import builder.dml.EntityData;
import builder.dml.EntityMetaData;
import builder.dml.EntityObjectData;
import builder.dml.builder.DMLQueryBuilder;
import event.EventListenerRegistry;
import event.EventType;
import event.action.ActionQueue;
import event.delete.DeleteEventListener;
import event.load.LoadEventListener;
import event.merge.MergeEventListener;
import event.persist.PersistEventListener;
import jdbc.JdbcTemplate;

import java.util.List;

public class EntityManagerImpl implements EntityManager {

    private final PersistenceContext persistenceContext;
    private final ActionQueue actionQueue;
    private final EventListenerRegistry eventListenerRegistry;
    private final Metamodel metamodel;

    public EntityManagerImpl(
            JdbcTemplate jdbcTemplate,
            PersistenceContext persistenceContext,
            Metamodel metamodel,
            DMLQueryBuilder dmlQueryBuilder
    ) {
        this.persistenceContext = persistenceContext;
        this.actionQueue = new ActionQueue();
        this.metamodel = metamodel;
        this.eventListenerRegistry = new EventListenerRegistry(this.actionQueue, metamodel, new EntityLoader(jdbcTemplate, dmlQueryBuilder));
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

        LoadEventListener<T> eventListener = (LoadEventListener<T>) this.eventListenerRegistry.getEventListenerGroup(EventType.LOAD).getEventListener();
        T findObject = eventListener.onLoad(entityData);

        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.LOADING);
        insertPersistenceContext(entityKey, entityData);
        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.MANAGED);

        return findObject;
    }

    @Override
    public void persist(Object entityInstance) {
        EntityData entityData = createEntityData(entityInstance);
        EntityKey entityKey = new EntityKey(entityData);

        EntityEntry entityEntry = this.persistenceContext.getEntityEntryMap(entityKey);

        if (entityEntry != null && !entityEntry.checkEntityStatus(EntityStatus.MANAGED)) {
            return;
        }

        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.SAVING);

        PersistEventListener eventListener = (PersistEventListener) this.eventListenerRegistry.getEventListenerGroup(EventType.PERSIST).getEventListener();
        eventListener.onPersist(entityData);

        insertPersistenceContext(entityKey, entityData);
        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.MANAGED);
    }

    @Override
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

        MergeEventListener mergeEventListener = (MergeEventListener) this.eventListenerRegistry.getEventListenerGroup(EventType.MERGE).getEventListener();
        mergeEventListener.onMerge(diffBuilderData);

        insertPersistenceContext(entityKey, entityData);
        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.MANAGED);
    }

    @Override
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

        DeleteEventListener deleteEventListener = (DeleteEventListener) this.eventListenerRegistry.getEventListenerGroup(EventType.DELETE).getEventListener();
        deleteEventListener.onDelete(entityData);

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
