package persistence.sql.dml.impl;

import boot.MetaModel;
import database.ConnectionHolder;
import event.EventListenerGroup;
import event.EventListenerRegistry;
import event.EventType;
import event.impl.DeleteEvent;
import event.impl.LoadEvent;
import event.impl.SaveOrUpdateEvent;
import jakarta.persistence.EntityExistsException;
import persistence.sql.clause.Clause;
import persistence.sql.context.EntityPersister;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.entity.EntityEntry;
import persistence.sql.entity.data.Status;
import persistence.sql.loader.EntityLoader;
import persistence.sql.transaction.Transaction;
import persistence.sql.transaction.impl.EntityTransaction;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;

public class DefaultEntityManager implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final MetaModel metaModel;
    private final Transaction transaction;
    private final EventListenerRegistry eventListenerRegistry;

    public DefaultEntityManager(PersistenceContext persistenceContext, MetaModel metaModel, EventListenerRegistry registry) {
        this.persistenceContext = persistenceContext;
        this.metaModel = metaModel;
        this.transaction = new EntityTransaction(this);
        this.eventListenerRegistry = registry;
    }

    @Override
    public Transaction getTransaction() {
        Connection connection = ConnectionHolder.getConnection();
        transaction.connect(connection);
        return transaction;
    }

    @Override
    public <T> void persist(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }

        if (!isNew(entity)) {
            throw new EntityExistsException("Entity already exists");
        }
        SaveOrUpdateEvent saveOrUpdateEvent = SaveOrUpdateEvent.create(entity, this);

        EventListenerGroup<?> eventListenerGroup = eventListenerRegistry.getEventListenerGroup(EventType.SAVE_OR_UPDATE);
        eventListenerGroup.fireEvent(saveOrUpdateEvent);

        if (!transaction.isActive()) {
            persistenceContext.cleanup();
        }
    }

    @Override
    public boolean isNew(Object entity) {
        EntityLoader<?> entityLoader = metaModel.entityLoader(entity.getClass());
        MetadataLoader<?> loader = entityLoader.getMetadataLoader();

        Field primaryKeyField = loader.getPrimaryKeyField();
        Object idValue = Clause.extractValue(primaryKeyField, entity);
        if (idValue == null) {
            return true;
        }

        return find(loader.getEntityType(), idValue) == null;
    }

    @Override
    public <T> T merge(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }

        if (isNew(entity)) {
            persist(entity);
            return entity;
        }

        SaveOrUpdateEvent event = SaveOrUpdateEvent.create(entity, this);
        eventListenerRegistry.getEventListenerGroup(EventType.SAVE_OR_UPDATE)
                .fireEvent(event);

        return entity;
    }

    @Override
    public <T> void remove(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }

        DeleteEvent deleteEvent = DeleteEvent.create(entity, this);
        eventListenerRegistry.getEventListenerGroup(EventType.DELETE)
                .fireEvent(deleteEvent);
    }

    @Override
    public <T> T find(Class<T> returnType, Object primaryKey) {
        if (primaryKey == null) {
            throw new IllegalArgumentException("Primary key must not be null");
        }

        LoadEvent<T> event = LoadEvent.create(returnType, primaryKey, this);
        EventListenerGroup<?> eventListenerGroup = eventListenerRegistry.getEventListenerGroup(EventType.LOAD);
        eventListenerGroup.fireEvent(event);

        EntityEntry entry = persistenceContext.getEntry(returnType, primaryKey);

        if (entry != null && entry.getEntityType() == returnType) {
            return returnType.cast(entry.getEntity());
        }

        return null;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        EntityLoader<T> entityLoader = metaModel.entityLoader(entityClass);
        EntityPersister<T> entityPersister = metaModel.entityPersister(entityClass);

        List<T> loadedEntities = entityLoader.loadAll();
        for (T loadedEntity : loadedEntities) {
            persistenceContext.addEntry(loadedEntity, Status.MANAGED, entityPersister);
        }

        return loadedEntities;
    }

    @Override
    public PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }

    @Override
    public <T> MetadataLoader<T> getMetadataLoader(Class<T> entityClass) {
        return metaModel.entityLoader(entityClass).getMetadataLoader();
    }

    @Override
    public <T> EntityPersister<T> getEntityPersister(Class<T> entityClass) {
        return metaModel.entityPersister(entityClass);
    }

    @Override
    public <T> EntityLoader<T> getEntityLoader(Class<T> entityClass) {
        return metaModel.entityLoader(entityClass);
    }

    @Override
    public void onFlush() {
        persistenceContext.dirtyCheck(metaModel);
        persistenceContext.cleanup();
    }
}
