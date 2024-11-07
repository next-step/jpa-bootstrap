package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.meta.Metamodel;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.Supplier;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final Metamodel metamodel;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate,
                             PersistenceContext persistenceContext,
                             Metamodel metamodel) {

        this.persistenceContext = persistenceContext;
        this.metamodel = metamodel;
        this.entityLoader = new EntityLoader(jdbcTemplate, metamodel);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final EntityKey entityKey = new EntityKey((Long) id, clazz);
        final EntityEntry entityEntry = getEntityEntryOrDefault(entityKey, () -> EntityEntry.loading((Serializable) id));

        if (entityEntry.isManaged()) {
            return clazz.cast(persistenceContext.getEntity(entityKey));
        }

        if (entityEntry.isNotReadable()) {
            throw new IllegalArgumentException("Entity is not managed: " + clazz.getSimpleName());
        }

        final T loaded = entityLoader.loadEntity(clazz, entityKey);
        storeEntityInContext(entityKey, loaded);
        updateEntryToManaged(entityKey, entityEntry);
        return loaded;
    }

    private EntityEntry getEntityEntryOrDefault(EntityKey entityKey, Supplier<EntityEntry> defaultEntrySupplier) {
        final EntityEntry entityEntry = persistenceContext.getEntityEntry(entityKey);
        if (entityEntry == null) {
            return defaultEntrySupplier.get();
        }

        return entityEntry;
    }

    @Override
    public void persist(Object entity) {
        final EntityPersister entityPersister = metamodel.findEntityPersister(entity.getClass());
        if (entityPersister.hasId(entity)) {
            throwIfNotManaged(entity, entityPersister);
            return;
        }

        doPersist(entity, entityPersister);
    }

    private void throwIfNotManaged(Object entity, EntityPersister entityPersister) {
        final EntityEntry entityEntry = persistenceContext.getEntityEntry(
                new EntityKey(entityPersister.getEntityId(entity), entity.getClass())
        );

        if (entityEntry == null) {
            throw new IllegalArgumentException("No Entity Entry with id: " + entityPersister.getEntityId(entity));
        }

        if (entityEntry.isManaged()) {
            return;
        }

        throw new IllegalArgumentException("Entity already persisted");
    }

    private void doPersist(Object entity, EntityPersister entityPersister) {
        final EntityEntry entityEntry = EntityEntry.inSaving();
        entityPersister.insert(entity);
        startManageEntity(entity, entityEntry, entityPersister.getEntityId(entity));

        for (TableAssociationDefinition association : entityPersister.getCollectionAssociations()) {
            final EntityCollectionPersister entityCollectionPersister = metamodel.findEntityCollectionPersister(association);
            final Collection<Object> childEntities = entityCollectionPersister.insertCollection(entity, association);
            childEntities.forEach(child -> {
                startManageEntity(child,
                        EntityEntry.inSaving(),
                        metamodel.findEntityPersister(child.getClass()).getEntityId(child));
            });
        }
    }

    private void startManageEntity(Object entity,
                                   EntityEntry entityEntry,
                                   Serializable id) {

        final EntityKey entityKey = new EntityKey(id, entity.getClass());
        entityEntry.bindId(id);
        entityEntry.updateStatus(Status.MANAGED);

        storeEntityInContext(entityKey, entity);
        updateEntryToManaged(entityKey, entityEntry);
    }

    @Override
    public void remove(Object entity) {
        final EntityPersister entityPersister = metamodel.findEntityPersister(entity.getClass());
        final EntityKey entityKey = new EntityKey(entityPersister.getEntityId(entity), entity.getClass());
        final EntityEntry entityEntry = persistenceContext.getEntityEntry(entityKey);
        checkManagedEntity(entity, entityEntry);

        entityEntry.updateStatus(Status.DELETED);
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entityKey);
    }

    @Override
    public <T> T merge(T entity) {
        final EntityPersister entityPersister = metamodel.findEntityPersister(entity.getClass());
        final EntityKey entityKey = new EntityKey(entityPersister.getEntityId(entity), entity.getClass());
        final EntityEntry entityEntry = persistenceContext.getEntityEntry(entityKey);
        checkManagedEntity(entity, entityEntry);

        final EntitySnapshot entitySnapshot = persistenceContext.getDatabaseSnapshot(entityKey);
        if (entitySnapshot.hasDirtyColumns(entity, metamodel.findTableDefinition(entity.getClass()))) {
            entityPersister.update(entity);
        }

        storeEntityInContext(entityKey, entity);
        updateEntryToManaged(entityKey, entityEntry);
        return entity;
    }

    @Override
    public void clear() {
        persistenceContext.clear();
    }

    private void checkManagedEntity(Object entity, EntityEntry entityEntry) {
        if (entityEntry == null) {
            throw new IllegalStateException("Can not find entity in persistence context: "
                    + entity.getClass().getSimpleName());
        }

        if (!entityEntry.isManaged()) {
            throw new IllegalArgumentException("Detached entity can not be merged: "
                    + entity.getClass().getSimpleName());
        }
    }

    private void storeEntityInContext(EntityKey entityKey, Object entity) {
        final TableDefinition tableDefinition = metamodel.findTableDefinition(entity.getClass());

        persistenceContext.addEntity(entityKey, entity);
        persistenceContext.addDatabaseSnapshot(entityKey, entity, tableDefinition);
    }

    private void updateEntryToManaged(EntityKey entityKey, EntityEntry entityEntry) {
        entityEntry.updateStatus(Status.MANAGED);
        persistenceContext.addEntry(entityKey, entityEntry);
    }

}
