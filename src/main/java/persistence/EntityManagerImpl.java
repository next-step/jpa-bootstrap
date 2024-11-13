package persistence;

import boot.Metamodel;
import builder.dml.DMLColumnData;
import builder.dml.EntityData;
import builder.dml.EntityMetaData;
import builder.dml.EntityObjectData;
import builder.dml.builder.DMLQueryBuilder;
import jdbc.JdbcTemplate;

import java.util.List;

public class EntityManagerImpl implements EntityManager {

    private final PersistenceContext persistenceContext;
    private final Metamodel metamodel;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate, PersistenceContext persistenceContext, Metamodel metamodel, DMLQueryBuilder dmlQueryBuilder) {
        this.entityLoader = new EntityLoader(jdbcTemplate, dmlQueryBuilder);
        this.persistenceContext = persistenceContext;
        this.metamodel = metamodel;
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        EntityKey entityKey = new EntityKey(id, clazz);
        EntityEntry entityEntry = this.persistenceContext.getEntityEntryMap(entityKey);

        if (entityEntry != null && entityEntry.checkEntityStatus(EntityStatus.MANAGED)) {
            EntityData persistEntityData = this.persistenceContext.findEntity(entityKey);
            return clazz.cast(persistEntityData.getEntityInstance());
        }

        T findObject = this.entityLoader.find(clazz, id);
        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.LOADING);

        EntityMetaData entityMetaData = this.metamodel.entityMetaData(clazz);
        EntityObjectData entityObjectData = new EntityObjectData(clazz, id);

        EntityData entityData = new EntityData(entityMetaData, entityObjectData);

        insertPersistenceContext(entityKey, entityData);
        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.MANAGED);

        return findObject;
    }

    @Override
    public void persist(Object entityInstance) {
        EntityMetaData entityMetaData = this.metamodel.entityMetaData(entityInstance.getClass());
        EntityObjectData entityObjectData = new EntityObjectData(entityInstance);

        EntityData entityData = new EntityData(entityMetaData, entityObjectData);
        EntityKey entityKey = new EntityKey(entityData);

        EntityEntry entityEntry = this.persistenceContext.getEntityEntryMap(entityKey);

        if (entityEntry != null && !entityEntry.checkEntityStatus(EntityStatus.MANAGED)) {
            return;
        }

        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.SAVING);

        this.metamodel.entityPersister(entityInstance.getClass()).persist(entityData);

        insertPersistenceContext(entityKey, entityData);
        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.MANAGED);
    }

    @Override
    public void merge(Object entityInstance) {
        EntityMetaData entityMetaData = this.metamodel.entityMetaData(entityInstance.getClass());
        EntityObjectData entityObjectData = new EntityObjectData(entityInstance);

        EntityData entityData = new EntityData(entityMetaData, entityObjectData);
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

        metamodel.entityPersister(entityInstance.getClass()).merge(diffBuilderData);

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
        metamodel.entityPersister(entityInstance.getClass()).remove(entityData);

        this.persistenceContext.deleteEntity(entityKey);
        this.persistenceContext.deleteDatabaseSnapshot(entityKey);
        this.persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.GONE);
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
}
