package persistence.session;

import jdbc.JdbcTemplate;
import persistence.entity.EntityEntry;
import persistence.entity.EntityKey;
import persistence.entity.EntityLoader;
import persistence.entity.EntityPersister;
import persistence.entity.EntitySnapshot;
import persistence.entity.PersistenceContext;
import persistence.entity.Status;
import persistence.event.ActionQueue;
import persistence.event.EventListenerGroupHandler;
import persistence.event.EventSource;
import persistence.event.PersistEvent;
import persistence.event.PersistEventListener;
import persistence.meta.Metamodel;

import java.io.Serializable;
import java.util.function.Supplier;

public class SessionImpl implements EventSource {
    private final PersistenceContext persistenceContext;
    private final Metamodel metamodel;
    private final EventListenerGroupHandler eventListenerGroupHandler;

    public SessionImpl(JdbcTemplate jdbcTemplate,
                       PersistenceContext persistenceContext,
                       EventListenerGroupHandler eventListenerGroupHandler,
                       Metamodel metamodel) {

        this.persistenceContext = persistenceContext;
        this.metamodel = metamodel;
        this.eventListenerGroupHandler = eventListenerGroupHandler;
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

        final T loaded = metamodel.findEntityLoader(clazz).loadEntity(clazz, entityKey);
        storeEntityInContext(entityKey, loaded);
        updateEntryToManaged(entityKey, entityEntry);
        return loaded;

//        return eventListenerGroupHandler.LOAD
//                .fireEventOnEachListener(
//                        entityEntry,
//                        () -> entityLoader.loadEntity(clazz, entityKey)
//                );
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

        eventListenerGroupHandler.PERSIST
                .fireEventOnEachListener(
                        PersistEvent.create(this, entity),
                        PersistEventListener::onPersist);

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
        if (entitySnapshot.hasDirtyColumns(entity, metamodel.findEntityPersister(entity.getClass()))) {
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

    @Override
    public Metamodel getMetamodel() {
        return metamodel;
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
        final EntityPersister persister = metamodel.findEntityPersister(entity.getClass());

        persistenceContext.addEntity(entityKey, entity);
        persistenceContext.addDatabaseSnapshot(entityKey, entity, persister);
    }

    private void updateEntryToManaged(EntityKey entityKey, EntityEntry entityEntry) {
        entityEntry.updateStatus(Status.MANAGED);
        persistenceContext.addEntry(entityKey, entityEntry);
    }

    @Override
    public void close() {
        clear();
    }

    @Override
    public ActionQueue getActionQueue() {
        return null;
    }

    @Override
    public PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }

    @Override
    public EntityPersister getEntityPersister(Class<?> clazz) {
        return metamodel.findEntityPersister(clazz);
    }
}
