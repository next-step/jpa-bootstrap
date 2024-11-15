package persistence.bootstrap;

import jdbc.JdbcTemplate;
import persistence.bootstrap.binder.CollectionLoaderBinder;
import persistence.bootstrap.binder.CollectionPersisterBinder;
import persistence.bootstrap.binder.EntityBinder;
import persistence.bootstrap.binder.EntityLoaderBinder;
import persistence.bootstrap.binder.EntityPersisterBinder;
import persistence.bootstrap.binder.EntityTableBinder;
import persistence.bootstrap.binder.RowMapperBinder;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.CollectionPersister;
import persistence.entity.persister.EntityPersister;
import persistence.event.EventListenerGroup;
import persistence.event.EventListenerRegistry;
import persistence.event.EventType;
import persistence.event.LoadEventListener;
import persistence.event.PersistEventListener;
import persistence.meta.EntityTable;

public class Metamodel {
    private final EntityTableBinder entityTableBinder;
    private final EntityLoaderBinder entityLoaderBinder;
    private final EntityPersisterBinder entityPersisterBinder;
    private final CollectionPersisterBinder collectionPersisterBinder;
    private final EventListenerRegistry eventListenerRegistry;

    public Metamodel(JdbcTemplate jdbcTemplate, EntityBinder entityBinder, EntityTableBinder entityTableBinder,
                     EventListenerRegistry eventListenerRegistry) {
        this.eventListenerRegistry = eventListenerRegistry;
        RowMapperBinder rowMapperBinder = new RowMapperBinder(entityBinder, entityTableBinder);
        CollectionLoaderBinder collectionLoaderBinder = new CollectionLoaderBinder(entityBinder, entityTableBinder, rowMapperBinder, jdbcTemplate);

        this.collectionPersisterBinder = new CollectionPersisterBinder(entityBinder, entityTableBinder, jdbcTemplate);
        this.entityLoaderBinder = new EntityLoaderBinder(entityBinder, entityTableBinder, collectionLoaderBinder, rowMapperBinder, jdbcTemplate);
        this.entityPersisterBinder = new EntityPersisterBinder(entityBinder, entityTableBinder, jdbcTemplate);
        this.entityTableBinder = entityTableBinder;
    }

    public EntityTable getEntityTable(Class<?> entityType) {
        return entityTableBinder.getEntityTable(entityType);
    }

    public EntityLoader getEntityLoader(Class<?> entityType) {
        return entityLoaderBinder.getEntityLoader(entityType);
    }

    public EntityPersister getEntityPersister(Class<?> entityType) {
        return entityPersisterBinder.getEntityPersister(entityType);
    }

    public CollectionPersister getCollectionPersister(Class<?> entityType, String columnName) {
        return collectionPersisterBinder.getCollectionPersister(entityType, columnName);
    }

    public EventListenerGroup<LoadEventListener> getLoadEventListenerGroup() {
        return eventListenerRegistry.getEventListenerGroup(EventType.LOAD);
    }

    public EventListenerGroup<PersistEventListener> getPersistEventListenerGroup() {
        return eventListenerRegistry.getEventListenerGroup(EventType.PERSIST);
    }

    public EventListenerGroup<PersistEventListener> getPersistOnflushEventListenerGroup() {
        return eventListenerRegistry.getEventListenerGroup(EventType.PERSIST_ONFLUSH);
    }

    public void close() {
        entityTableBinder.clear();
        entityLoaderBinder.clear();
        entityPersisterBinder.clear();
        collectionPersisterBinder.clear();
    }
}
