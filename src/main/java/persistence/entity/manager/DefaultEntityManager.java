package persistence.entity.manager;

import persistence.bootstrap.Metamodel;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.event.DeleteEvent;
import persistence.event.DeleteEventListener;
import persistence.event.LoadEvent;
import persistence.event.LoadEventListener;
import persistence.event.PersistEvent;
import persistence.event.PersistEventListener;
import persistence.event.UpdateEvent;
import persistence.event.UpdateEventListener;
import persistence.meta.EntityTable;

import java.util.Queue;

public class DefaultEntityManager implements EntityManager {
    public static final String NOT_PERSISTABLE_STATUS_FAILED_MESSAGE = "엔티티가 영속화 가능한 상태가 아닙니다.";
    public static final String NOT_REMOVABLE_STATUS_FAILED_MESSAGE = "엔티티가 제거 가능한 상태가 아닙니다.";

    private final Metamodel metamodel;
    private final PersistenceContext persistenceContext;

    public DefaultEntityManager(Metamodel metamodel) {
        this.metamodel = metamodel;
        this.persistenceContext = new PersistenceContext();
    }

    @Override
    public <T> T find(Class<T> entityType, Object id) {
        final LoadEvent<T> loadEvent = new LoadEvent<>(metamodel, persistenceContext, entityType, id);
        metamodel.getLoadEventListenerGroup().doEvent(loadEvent, LoadEventListener::onLoad);
        return loadEvent.getResult();
    }

    @Override
    public <T> void persist(T entity) {
        validatePersist(entity);

        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());
        if (entityTable.isIdGenerationFromDatabase()) {
            final PersistEvent<T> persistEvent = new PersistEvent<>(metamodel, persistenceContext, entity);
            metamodel.getPersistEventListenerGroup().doEvent(persistEvent, PersistEventListener::onPersist);
            return;
        }

        persistenceContext.addEntity(entity, entityTable.getIdValue(entity));
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
        persistenceContext.removeEntity(entity, entityTable.getIdValue(entity));
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
        if (entityEntry != null && !entityEntry.isPersistable()) {
            throw new IllegalStateException(NOT_PERSISTABLE_STATUS_FAILED_MESSAGE);
        }
    }

    private <T> void persistAll() {
        final Queue<Object> persistQueue = persistenceContext.getPersistQueue();
        while (!persistQueue.isEmpty()) {
            final T entity = (T) persistQueue.poll();

            final PersistEvent<T> persistEvent = new PersistEvent<>(metamodel, persistenceContext, entity);
            metamodel.getPersistOnflushEventListenerGroup().doEvent(persistEvent, PersistEventListener::onPersist);
        }
    }

    private <T> void deleteAll() {
        final Queue<Object> removeQueue = persistenceContext.getRemoveQueue();
        while (!removeQueue.isEmpty()) {
            final T entity = (T) removeQueue.poll();

            final DeleteEvent<T> deleteEvent = new DeleteEvent<>(metamodel, entity);
            metamodel.getDeleteEventListenerGroup().doEvent(deleteEvent, DeleteEventListener::onDelete);
        }
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
