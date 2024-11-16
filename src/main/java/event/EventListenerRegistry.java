package event;

import boot.Metamodel;
import event.action.ActionQueue;
import event.listener.EventListener;
import persistence.EntityLoader;

public class EventListenerRegistry {

    private final EventListenerGroup eventListenerGroup;

    public EventListenerRegistry(ActionQueue actionQueue, Metamodel metamodel, EntityLoader entityLoader) {
        eventListenerGroup = new EventListenerGroup(actionQueue, metamodel, entityLoader);
    }

    public EventListener<?> getEventListener(EventType eventType) {
        return eventListenerGroup.getEventListener(eventType);
    }

    public void execute() {
        this.eventListenerGroup.execute();
    }

}
