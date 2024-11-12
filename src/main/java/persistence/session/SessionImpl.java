package persistence.session;

import persistence.entity.EntityEntry;
import persistence.entity.EntityKey;
import persistence.entity.EntityPersister;
import persistence.entity.PersistenceContext;
import persistence.event.*;
import persistence.meta.Metamodel;

import java.io.Serializable;
import java.util.function.Supplier;

public class SessionImpl implements EventSource {
    private final PersistenceContext persistenceContext;
    private final Metamodel metamodel;
    private final EventListenerGroupHandler eventListenerGroupHandler;

    public SessionImpl(PersistenceContext persistenceContext,
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

        check(entityEntry.isNotReadable(), "Entity is not managed: " + clazz.getSimpleName());

        final T entity = metamodel.findEntityLoader(clazz).loadEntity(clazz, entityKey);
        eventListenerGroupHandler.LOAD
                .fireEventOnEachListener(
                        LoadEvent.create(this, (Serializable) id, entity, entityEntry),
                        LoadEventListener::onLoad
                );

        return entity;
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
            final EntityEntry entityEntry = persistenceContext.getEntityEntry(
                    new EntityKey(entityPersister.getEntityId(entity), entity.getClass())
            );

            checkManagedEntity(entity, entityEntry);
            return;
        }

        eventListenerGroupHandler.PERSIST
                .fireEventOnEachListener(
                        PersistEvent.create(this, entity),
                        PersistEventListener::onPersist);

    }

    @Override
    public void remove(Object entity) {
        final EntityPersister entityPersister = metamodel.findEntityPersister(entity.getClass());
        final EntityKey entityKey = new EntityKey(entityPersister.getEntityId(entity), entity.getClass());
        final EntityEntry entityEntry = persistenceContext.getEntityEntry(entityKey);
        checkManagedEntity(entity, entityEntry);

        eventListenerGroupHandler.DELETE
                .fireEventOnEachListener(
                        DeleteEvent.create(this, entity, entityEntry),
                        DeleteEventListener::onDelete
                );
    }

    @Override
    public <T> T merge(T entity) {
        final EntityPersister entityPersister = metamodel.findEntityPersister(entity.getClass());
        final EntityKey entityKey = new EntityKey(entityPersister.getEntityId(entity), entity.getClass());
        final EntityEntry entityEntry = persistenceContext.getEntityEntry(entityKey);
        checkManagedEntity(entity, entityEntry);

        eventListenerGroupHandler.MERGE
                .fireEventOnEachListener(
                        MergeEvent.create(this, entity, entityEntry),
                        MergeEventListener::onMerge);

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
        check(entityEntry == null,
                "Can not find entry in persistence context: " + entity.getClass().getSimpleName());

        check(!entityEntry.isManaged(),
                "Detached entity can not be merged: " + entity.getClass().getSimpleName());
    }

    private void check(boolean condition, String reason) {
        if (condition) {
            throw new IllegalArgumentException(reason);
        }
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
    public EntityPersister findEntityPersister(Class<?> clazz) {
        return metamodel.findEntityPersister(clazz);
    }
}
