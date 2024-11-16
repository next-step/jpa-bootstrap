package event;

import boot.Metamodel;
import builder.dml.EntityData;
import event.action.ActionQueue;
import event.listener.*;
import persistence.EntityLoader;

import java.util.HashMap;
import java.util.Map;

public class EventListenerGroup<T> {

    private final Map<EventType, EventListener<?>> eventListeners = new HashMap<>();
    private ActionQueue actionQueue;

    public EventListenerGroup(Metamodel metamodel, EntityLoader entityLoader) {
        this.eventListeners.put(EventType.LOAD, new LoadEventListenerImpl<>(entityLoader));
        this.eventListeners.put(EventType.PERSIST, new PersistEventListenerImpl<>(metamodel));
        this.eventListeners.put(EventType.MERGE, new MergeEventListenerImpl<>(metamodel));
        this.eventListeners.put(EventType.DELETE, new DeleteEventListenerImpl<>(metamodel));
    }

    @SuppressWarnings("unchecked")
    public T handleEvent(EventType eventType, EntityData entityData) {
        return (T) this.eventListeners.get(eventType).handleEvent(entityData);
    }

    public void execute() {
        this.actionQueue.execute();
    }

    public void setActionQueue(ActionQueue actionQueue) {
        this.actionQueue = actionQueue;
        setActionQueueAllListener();
    }

    private void setActionQueueAllListener() {
        for (EventListener<?> listener : eventListeners.values()) {
            listener.setActionQueue(this.actionQueue);
        }
    }

}
