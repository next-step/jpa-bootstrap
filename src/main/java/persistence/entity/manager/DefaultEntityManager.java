package persistence.entity.manager;

import persistence.action.ActionQueue;
import persistence.bootstrap.Metamodel;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.event.delete.DeleteEvent;
import persistence.event.delete.DeleteEventListener;
import persistence.event.load.LoadEvent;
import persistence.event.load.LoadEventListener;
import persistence.event.persist.PersistEvent;
import persistence.event.persist.PersistEventListener;
import persistence.event.update.UpdateEvent;
import persistence.event.update.UpdateEventListener;

public class DefaultEntityManager implements EntityManager {
    public static final String NOT_PERSISTABLE_STATUS_FAILED_MESSAGE = "엔티티가 영속화 가능한 상태가 아닙니다.";
    public static final String NOT_REMOVABLE_STATUS_FAILED_MESSAGE = "엔티티가 제거 가능한 상태가 아닙니다.";

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
        return loadEvent.getResult();
    }

    @Override
    public <T> void persist(T entity) {
        final PersistEvent<T> persistEvent = new PersistEvent<>(metamodel, persistenceContext, actionQueue, entity);
        metamodel.getPersistEventListenerGroup().doEvent(persistEvent, PersistEventListener::onPersist);
    }

    @Override
    public <T> void remove(T entity) {
        final DeleteEvent<T> deleteEvent = new DeleteEvent<>(metamodel, persistenceContext, actionQueue, entity);
        metamodel.getDeleteEventListenerGroup().doEvent(deleteEvent, DeleteEventListener::onDelete);
    }

    @Override
    public void flush() {
        actionQueue.executeAll();
        updateAll();
    }

    @Override
    public void clear() {
        persistenceContext.clear();
    }

    private void updateAll() {
        persistenceContext.getAllEntity()
                .forEach(this::update);
    }

    private <T> void update(T entity) {
        final UpdateEvent<T> updateEvent = new UpdateEvent<>(metamodel, persistenceContext, entity);
        metamodel.getUpdateEventListenerGroup().doEvent(updateEvent, UpdateEventListener::onUpdate);
    }

}
