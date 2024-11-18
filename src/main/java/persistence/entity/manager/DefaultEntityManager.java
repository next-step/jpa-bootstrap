package persistence.entity.manager;

import persistence.action.ActionQueue;
import persistence.bootstrap.Metamodel;
import persistence.entity.loader.EntityLoader;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.event.EventDispatcher;
import persistence.event.delete.DeleteEvent;
import persistence.event.load.LoadEvent;
import persistence.event.persist.PersistEvent;
import persistence.event.update.UpdateEvent;
import persistence.meta.EntityTable;

public class DefaultEntityManager implements EntityManager {
    private final Metamodel metamodel;
    private final PersistenceContext persistenceContext;
    private final ActionQueue actionQueue;
    private final EventDispatcher eventDispatcher;

    public DefaultEntityManager(Metamodel metamodel) {
        this.metamodel = metamodel;
        this.persistenceContext = new PersistenceContext();
        this.actionQueue = new ActionQueue();
        this.eventDispatcher = new EventDispatcher(metamodel, persistenceContext, actionQueue);
    }

    @Override
    public <T> T find(Class<T> entityType, Object id) {
        final LoadEvent<T> loadEvent = new LoadEvent<>(entityType, id);
        eventDispatcher.dispatch(loadEvent);

        persistenceContext.addEntity(loadEvent.getResult(), id);
        return loadEvent.getResult();
    }

    @Override
    public <T> void persist(T entity) {
        final PersistEvent<T> persistEvent = new PersistEvent<>(entity);
        eventDispatcher.dispatch(persistEvent);

        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());
        persistenceContext.addEntity(entity, entityTable.getIdValue(entity));
        persistenceContext.createOrUpdateStatus(entity, EntityStatus.MANAGED);
    }

    @Override
    public <T> void remove(T entity) {
        final DeleteEvent<T> deleteEvent = new DeleteEvent<>(entity);
        eventDispatcher.dispatch(deleteEvent);

        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());
        persistenceContext.removeEntity(entity, entityTable.getIdValue(entity));
    }

    @Override
    public <T> void merge(T entity) {
        final EntityLoader entityLoader = metamodel.getEntityLoader(entity.getClass());
        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());
        final Object idValue = entityTable.getIdValue(entity);

        if (idValue == null || entityLoader.load(idValue) == null) {
            persist(entity);
            return;
        }

        final Object snapshot = persistenceContext.getSnapshot(entity.getClass(), entityTable.getIdValue(entity));
        if (snapshot == null) {
            return;
        }

        final UpdateEvent<T> updateEvent = new UpdateEvent<>(entity);
        eventDispatcher.dispatch(updateEvent);

        persistenceContext.addEntity(entity, entityTable.getIdValue(entity));
        persistenceContext.createOrUpdateStatus(entity, EntityStatus.MANAGED);
    }

    @Override
    public void flush() {
        actionQueue.executeAll();
        updateAllEntity();
    }

    @Override
    public void clear() {
        persistenceContext.clear();
        actionQueue.clear();
    }

    private void updateAllEntity() {
        persistenceContext.getAllEntity()
                .forEach(this::update);
    }

    private <T> void update(T entity) {
        final UpdateEvent<T> updateEvent = new UpdateEvent<>(entity);
        eventDispatcher.dispatch(updateEvent);
    }
}
