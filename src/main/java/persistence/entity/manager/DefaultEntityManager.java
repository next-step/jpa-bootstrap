package persistence.entity.manager;

import persistence.action.ActionQueue;
import persistence.bootstrap.Metamodel;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.event.clear.ClearEvent;
import persistence.event.clear.ClearEventListener;
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
import persistence.meta.EntityTable;

public class DefaultEntityManager implements EntityManager {
    private final Metamodel metamodel;
    private final PersistenceContext persistenceContext;
    private final ActionQueue actionQueue;

    public DefaultEntityManager(Metamodel metamodel) {
        this.metamodel = metamodel;
        this.persistenceContext = new PersistenceContext();
        this.actionQueue = new ActionQueue();
    }

    @Override
    public <T> T find(Class<T> entityType, Object id) {
        final LoadEvent<T> loadEvent = new LoadEvent<>(metamodel, persistenceContext, entityType, id);
        metamodel.getLoadEventListenerGroup().doEvent(loadEvent, LoadEventListener::onLoad);

        persistenceContext.addEntity(loadEvent.getResult(), id);
        return loadEvent.getResult();
    }

    @Override
    public <T> void persist(T entity) {
        final PersistEvent<T> persistEvent = new PersistEvent<>(metamodel, persistenceContext, actionQueue, entity);
        metamodel.getPersistEventListenerGroup().doEvent(persistEvent, PersistEventListener::onPersist);

        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());
        persistenceContext.addEntity(entity, entityTable.getIdValue(entity));
        persistenceContext.createOrUpdateStatus(entity, EntityStatus.MANAGED);
    }

    @Override
    public <T> void remove(T entity) {
        final DeleteEvent<T> deleteEvent = new DeleteEvent<>(metamodel, persistenceContext, actionQueue, entity);
        metamodel.getDeleteEventListenerGroup().doEvent(deleteEvent, DeleteEventListener::onDelete);

        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());
        persistenceContext.removeEntity(entity, entityTable.getIdValue(entity));
    }

    @Override
    public <T> void merge(T entity) {
        final MergeEvent<T> mergeEvent = new MergeEvent<>(metamodel, persistenceContext, actionQueue, entity);
        metamodel.getMergeEventListenerGroup().doEvent(mergeEvent, MergeEventListener::onMerge);

        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());
        persistenceContext.addEntity(entity, entityTable.getIdValue(entity));
        persistenceContext.createOrUpdateStatus(entity, EntityStatus.MANAGED);
    }

    @Override
    public void flush() {
        final FlushEvent flushEvent = new FlushEvent(metamodel, persistenceContext, actionQueue);
        metamodel.getFlushEventListenerGroup().doEvent(flushEvent, FlushEventListener::onFlush);
    }

    @Override
    public void clear() {
        final ClearEvent clearEvent = new ClearEvent(persistenceContext, actionQueue);
        metamodel.getClearEventListenerGroup().doEvent(clearEvent, ClearEventListener::onClear);
    }
}
