package event;

import boot.Metamodel;
import event.action.ActionQueue;
import event.listener.*;
import persistence.EntityLoader;

import java.util.HashMap;
import java.util.Map;

public class EventListenerGroup {

    private final Map<EventType, EventListener<?>> eventListeners = new HashMap<>();
    private final ActionQueue actionQueue;

    public EventListenerGroup(ActionQueue actionQueue, Metamodel metamodel, EntityLoader entityLoader) {
        this.actionQueue = actionQueue;
        this.eventListeners.put(EventType.LOAD, new LoadEventListenerImpl<>(entityLoader));
        this.eventListeners.put(EventType.PERSIST, new PersistEventListenerImpl<>(actionQueue, metamodel));
        this.eventListeners.put(EventType.MERGE, new MergeEventListenerImpl<>(actionQueue, metamodel));
        this.eventListeners.put(EventType.DELETE, new DeleteEventListenerImpl<>(actionQueue, metamodel));
    }

    public EventListener<?> getEventListener(EventType eventType) {
        return eventListeners.get(eventType);
    }

    public void execute() {
        this.actionQueue.execute();
    }

}
