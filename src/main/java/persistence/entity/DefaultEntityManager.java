package persistence.entity;

import persistence.bootstrap.Metamodel;
import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DefaultEntityManager implements EntityManager {
    public static final String NOT_PERSISTABLE_STATUS_FAILED_MESSAGE = "엔티티가 영속화 가능한 상태가 아닙니다.";
    public static final String NOT_REMOVABLE_STATUS_FAILED_MESSAGE = "엔티티가 제거 가능한 상태가 아닙니다.";

    private final PersistenceContext persistenceContext;
    private final Metamodel metamodel;

    public DefaultEntityManager(PersistenceContext persistenceContext, Metamodel metamodel) {
        this.persistenceContext = persistenceContext;
        this.metamodel = metamodel;
    }

    @Override
    public <T> T find(Class<T> entityType, Object id) {
        final T managedEntity = persistenceContext.getEntity(entityType, id);
        if (Objects.nonNull(managedEntity)) {
            return managedEntity;
        }

        final EntityLoader entityLoader = metamodel.getEntityLoader(entityType);
        final T entity = entityLoader.load(id);
        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());

        persistenceContext.addEntity(entity, entityTable);
        return entity;
    }

    @Override
    public void persist(Object entity) {
        validatePersist(entity);

        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());
        if (entityTable.isIdGenerationFromDatabase()) {
            persistImmediately(entity, entityTable);
            return;
        }

        persistenceContext.addEntity(entity, entityTable);
        persistenceContext.createOrUpdateStatus(entity, EntityStatus.MANAGED);
        persistenceContext.addToPersistQueue(entity);
    }

    @Override
    public void remove(Object entity) {
        final EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        if (!entityEntry.isRemovable()) {
            throw new IllegalStateException(NOT_REMOVABLE_STATUS_FAILED_MESSAGE);
        }

        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());
        persistenceContext.removeEntity(entity, entityTable);
        persistenceContext.addToRemoveQueue(entity);
    }

    @Override
    public void flush() {
        persistAll();
        deleteAll();
        updateAll();
    }

    @Override
    public void clear() {
        persistenceContext.clear();
    }

    private void validatePersist(Object entity) {
        final EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        if (Objects.nonNull(entityEntry) && !entityEntry.isPersistable()) {
            throw new IllegalStateException(NOT_PERSISTABLE_STATUS_FAILED_MESSAGE);
        }
    }

    private void persistImmediately(Object entity, EntityTable entityTable) {
        persist(entity, entityTable);
        persistenceContext.addEntity(entity, entityTable);
        persistenceContext.createOrUpdateStatus(entity, EntityStatus.MANAGED);
    }

    private void persistAll() {
        final Queue<Object> persistQueue = persistenceContext.getPersistQueue();
        while (!persistQueue.isEmpty()) {
            final Object entity = persistQueue.poll();
            final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());

            persist(entity, entityTable);
            persistenceContext.createOrUpdateStatus(entity, EntityStatus.MANAGED);
        }
    }

    private void persist(Object entity, EntityTable entityTable) {
        final EntityPersister entityPersister = metamodel.getEntityPersister(entity.getClass());
        entityPersister.insert(entity);
        if (entityTable.isOneToMany()) {
            final CollectionPersister collectionPersister = metamodel.getCollectionPersister(
                    entity.getClass(), entityTable.getAssociationColumnName());
            collectionPersister.insert(entityTable.getAssociationColumnValue(entity), entity);
        }
    }

    private void deleteAll() {
        final Queue<Object> removeQueue = persistenceContext.getRemoveQueue();
        while (!removeQueue.isEmpty()) {
            final Object entity = removeQueue.poll();
            final EntityPersister entityPersister = metamodel.getEntityPersister(entity.getClass());
            entityPersister.delete(entity);
        }
    }

    private void updateAll() {
        persistenceContext.getAllEntity()
                .forEach(this::update);
    }

    private void update(Object entity) {
        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());
        final Object snapshot = persistenceContext.getSnapshot(entity.getClass(), entityTable.getIdValue(entity));
        if (Objects.isNull(snapshot)) {
            return;
        }

        final List<EntityColumn> dirtiedEntityColumns = getDirtiedEntityColumns(entity, snapshot);
        if (dirtiedEntityColumns.isEmpty()) {
            return;
        }

        final EntityPersister entityPersister = metamodel.getEntityPersister(entity.getClass());
        entityPersister.update(entity, dirtiedEntityColumns);
        persistenceContext.addEntity(entity, entityTable);
    }

    private List<EntityColumn> getDirtiedEntityColumns(Object entity, Object snapshot) {
        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());
        final EntityTable snapshotEntityTable = metamodel.getEntityTable(snapshot.getClass());

        return IntStream.range(0, entityTable.getColumnCount())
                .filter(i -> isDirtied(entityTable.getEntityColumn(i), snapshotEntityTable.getEntityColumn(i), entity, snapshot))
                .mapToObj(entityTable::getEntityColumn)
                .collect(Collectors.toList());
    }

    private boolean isDirtied(EntityColumn entityColumn, EntityColumn snapshotEntityColumn, Object entity, Object snapshot) {
        return !Objects.equals(entityColumn.getValue(entity), snapshotEntityColumn.getValue(snapshot));
    }
}
