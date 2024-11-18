package persistence.event;

import persistence.action.ActionQueue;
import persistence.bootstrap.Metamodel;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.event.delete.DefaultDeleteEventListener;
import persistence.event.load.DefaultLoadEventListener;
import persistence.event.persist.DefaultPersistEventListener;
import persistence.event.update.DefaultUpdateEventListener;

import java.util.HashMap;
import java.util.Map;

public class EventDispatcher {
    private final Map<EventType<?>, EventListenerGroup<EventListener>> eventListenerGroupRegistry = new HashMap<>();

    public EventDispatcher(Metamodel metamodel, PersistenceContext persistenceContext, ActionQueue actionQueue) {
        registerLoadEventListener(metamodel, persistenceContext);
        registerPersistEventListener(metamodel, persistenceContext, actionQueue);
        registerDeleteEventListener(metamodel, persistenceContext, actionQueue);
        registerUpdateEventListener(metamodel, persistenceContext, actionQueue);
    }

    public <T> void dispatch(Event<T> event) {
        final EventListenerGroup<EventListener> eventListenerGroup = eventListenerGroupRegistry.get(event.getEventType());
        eventListenerGroup.doEvent(event, EventListener::on);
    }

    public void clear() {
        eventListenerGroupRegistry.clear();
    }

    private void registerLoadEventListener(Metamodel metamodel, PersistenceContext persistenceContext) {
        final EventListenerGroup<EventListener> eventListenerGroup = new EventListenerGroup<>(EventType.LOAD);
        eventListenerGroup.appendListener(new DefaultLoadEventListener(metamodel, persistenceContext));
        eventListenerGroupRegistry.put(EventType.LOAD, eventListenerGroup);
    }

    private void registerPersistEventListener(Metamodel metamodel, PersistenceContext persistenceContext, ActionQueue actionQueue) {
        final EventListenerGroup<EventListener> eventListenerGroup = new EventListenerGroup<>(EventType.PERSIST);
        eventListenerGroup.appendListener(new DefaultPersistEventListener(metamodel, persistenceContext, actionQueue));
        eventListenerGroupRegistry.put(EventType.PERSIST, eventListenerGroup);
    }

    private void registerDeleteEventListener(Metamodel metamodel, PersistenceContext persistenceContext, ActionQueue actionQueue) {
        final EventListenerGroup<EventListener> eventListenerGroup = new EventListenerGroup<>(EventType.DELETE);
        eventListenerGroup.appendListener(new DefaultDeleteEventListener(metamodel, persistenceContext, actionQueue));
        eventListenerGroupRegistry.put(EventType.DELETE, eventListenerGroup);
    }

    private void registerUpdateEventListener(Metamodel metamodel, PersistenceContext persistenceContext, ActionQueue actionQueue) {
        final EventListenerGroup<EventListener> eventListenerGroup = new EventListenerGroup<>(EventType.UPDATE);
        eventListenerGroup.appendListener(new DefaultUpdateEventListener(metamodel, persistenceContext, actionQueue));
        eventListenerGroupRegistry.put(EventType.UPDATE, eventListenerGroup);
    }
}
