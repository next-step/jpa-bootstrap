package persistence.session;

import persistence.action.ActionQueue;
import persistence.entity.CollectionPersister;
import persistence.entity.EntityEntry;
import persistence.entity.EntityKey;
import persistence.entity.EntityLoader;
import persistence.entity.EntityPersister;
import persistence.entity.PersistenceContext;
import persistence.event.EventSource;
import persistence.event.SessionService;
import persistence.event.delete.DeleteEvent;
import persistence.event.delete.DeleteEventListener;
import persistence.event.flush.FlushEvent;
import persistence.event.flush.FlushEventListener;
import persistence.event.load.LoadEvent;
import persistence.event.load.LoadEventListener;
import persistence.event.merge.MergeEvent;
import persistence.event.merge.MergeEventListener;
import persistence.event.persist.PersistEvent;
import persistence.event.persist.PersistEventListener;
import persistence.meta.Metamodel;
import persistence.sql.definition.TableAssociationDefinition;

import java.io.Serializable;
import java.util.function.Supplier;

public class SessionImpl implements EventSource {
    private final PersistenceContext persistenceContext;
    private final Metamodel metamodel;
    private final SessionService sessionService;
    private final ActionQueue actionQueue;

    public SessionImpl(PersistenceContext persistenceContext,
                       Metamodel metamodel,
                       SessionService sessionService,
                       ActionQueue actionQueue) {

        this.persistenceContext = persistenceContext;
        this.metamodel = metamodel;
        this.sessionService = sessionService;
        this.actionQueue = actionQueue;
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final EntityKey entityKey = new EntityKey((Long) id, clazz);
        final EntityEntry entityEntry = getEntityEntryOrDefault(entityKey, () -> EntityEntry.loading((Serializable) id));

        if (entityEntry.isManaged()) {
            return clazz.cast(persistenceContext.getEntity(entityKey));
        }

        check(entityEntry.isNotReadable(), "Entity is not managed: " + clazz.getSimpleName());

        LoadEvent<T> event = new LoadEvent<>(this, clazz, (Serializable) id, entityEntry);
        sessionService.LOAD.fireEventOnEachListener(
                event,
                LoadEventListener::onLoad
        );

        return event.getResultEntity();
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

        sessionService.PERSIST.fireEventOnEachListener(
                PersistEvent.create(this, entity),
                PersistEventListener::onPersist
        );

    }

    @Override
    public void remove(Object entity) {
        final EntityPersister entityPersister = metamodel.findEntityPersister(entity.getClass());
        final EntityKey entityKey = new EntityKey(entityPersister.getEntityId(entity), entity.getClass());
        final EntityEntry entityEntry = persistenceContext.getEntityEntry(entityKey);
        checkManagedEntity(entity, entityEntry);

        sessionService.DELETE.fireEventOnEachListener(
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

        sessionService.MERGE.fireEventOnEachListener(
                MergeEvent.create(this, entity, entityEntry),
                MergeEventListener::onMerge
        );

        return entity;
    }

    @Override
    public void flush() {
        sessionService.FLUSH.fireEventOnEachListener(
                new FlushEvent(this),
                FlushEventListener::onFlush
        );
    }

    @Override
    public void clear() {
        persistenceContext.clear();
        actionQueue.clear();
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
        return actionQueue;
    }

    @Override
    public PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }

    @Override
    public EntityPersister findEntityPersister(Class<?> clazz) {
        return metamodel.findEntityPersister(clazz);
    }

    @Override
    public CollectionPersister findCollectionPersister(TableAssociationDefinition association) {
        return metamodel.findCollectionPersister(association);
    }

    @Override
    public EntityLoader findEntityLoader(Class<?> clazz) {
        return metamodel.findEntityLoader(clazz);
    }
}
