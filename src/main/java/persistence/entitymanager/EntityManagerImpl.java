package persistence.entitymanager;

import persistence.bootstrap.Metadata;
import persistence.bootstrap.Metamodel;
import persistence.entity.context.EntityEntries;
import persistence.entity.context.FirstLevelCache;
import persistence.entity.context.PersistenceContext;
import persistence.entity.context.PersistenceContextImpl;
import persistence.entitymanager.action.ActionQueue;
import persistence.entitymanager.event.EventListenerRegistry;
import persistence.entitymanager.event.event.DeleteEvent;
import persistence.entitymanager.event.event.LoadEvent;
import persistence.entitymanager.event.event.PersistEvent;
import persistence.entitymanager.event.listeners.DeleteEventListener;
import persistence.entitymanager.event.listeners.LoadEventListener;
import persistence.entitymanager.event.listeners.PersistEventListener;

import static persistence.entitymanager.event.event.EventType.*;

public class EntityManagerImpl extends AbstractEntityManager {
    private final PersistenceContext persistenceContext;
    private final EventListenerRegistry eventListenerRegistry;
    private final ActionQueue actionQueue;
    // 액션큐

    public static EntityManager newEntityManager(
            Metamodel metamodel,
            Metadata metadata,
            EventListenerRegistry eventListenerRegistry) {
        return new EntityManagerImpl(metadata, metamodel, eventListenerRegistry);
    }

    private EntityManagerImpl(
            Metadata metadata,
            Metamodel metamodel,
            EventListenerRegistry eventListenerRegistry) {
        super(metamodel);

        this.persistenceContext = new PersistenceContextImpl(
                metadata,
                new FirstLevelCache(),
                new EntityEntries(),
                this);
        this.eventListenerRegistry = eventListenerRegistry;
        this.actionQueue = new ActionQueue(persistenceContext);
    }

    @Override
    public <T> T find(Class<T> entityClass, Long id) {
        LoadEvent event = new LoadEvent(entityClass, id, persistenceContext);
        fireLoad(event);
        return (T) event.getResult();
    }

    private void fireLoad(LoadEvent event) {
        eventListenerRegistry.getEventListenerGroup(LOAD)
                .fireEventOnEachListener(event, LoadEventListener::onLoad);
    }

    @Override
    public void persist(Object entity) {
        PersistEvent event = new PersistEvent(entity, persistenceContext);
        firePersist(event);
    }

    private void firePersist(PersistEvent event) {
        eventListenerRegistry.getEventListenerGroup(PERSIST)
                .fireEventOnEachListener(event, PersistEventListener::onPersist);
    }

    @Override
    public void remove(Object entity) {
        DeleteEvent event = new DeleteEvent(entity, this);
        fireDelete(event);
    }

    private void fireDelete(DeleteEvent event) {
        eventListenerRegistry.getEventListenerGroup(DELETE)
                .fireEventOnEachListener(event, DeleteEventListener::onDelete);
    }

    @Override
    public void flush() {
        actionQueue.flush();
    }

    @Override
    public void clear() {
        actionQueue.clear();
    }

    @Override
    public ActionQueue getActionQueue() {
        return actionQueue;
    }
}
