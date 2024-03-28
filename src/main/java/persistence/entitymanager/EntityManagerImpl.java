package persistence.entitymanager;

import persistence.bootstrap.Metadata;
import persistence.bootstrap.Metamodel;
import persistence.entitymanager.listener.EventListenerRegistry;
import persistence.entitymanager.listener.events.DeleteEvent;
import persistence.entitymanager.listener.events.LoadEvent;
import persistence.entitymanager.listener.events.PersistEvent;
import persistence.entitymanager.listener.listeners.DeleteEventListener;
import persistence.entitymanager.listener.listeners.LoadEventListener;
import persistence.entitymanager.listener.listeners.PersistEventListener;

import static persistence.entitymanager.listener.events.EventType.*;

public class EntityManagerImpl extends AbstractEntityManager {
    private final EventListenerRegistry eventListenerRegistry;

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
        super(metamodel, metadata);

        this.eventListenerRegistry = eventListenerRegistry;
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
        PersistEvent event = new PersistEvent(entity, this);
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
}
