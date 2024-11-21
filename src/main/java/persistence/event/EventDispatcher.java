package persistence.event;

import persistence.action.ActionQueue;
import persistence.bootstrap.Metamodel;
import persistence.entity.manager.factory.PersistenceContext;

public class EventDispatcher {
    private final EventListenerRegistry eventListenerRegistry;

    public EventDispatcher(Metamodel metamodel, PersistenceContext persistenceContext, ActionQueue actionQueue) {
        this.eventListenerRegistry = new EventListenerRegistry(metamodel, persistenceContext, actionQueue);
    }

    public <T> void dispatch(Event<T> event) {
        final EventListenerGroup<EventListener> eventListenerGroup =
                eventListenerRegistry.eventListenerGroupRegistry.get(event.getEventType());
        eventListenerGroup.doEvent(event, EventListener::on);
    }
}
