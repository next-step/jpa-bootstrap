package event;

import boot.Metamodel;
import event.action.ActionQueue;
import event.delete.DeleteEventListenerImpl;
import event.load.LoadEventListenerImpl;
import event.merge.MergeEventListenerImpl;
import event.persist.PersistEventListenerImpl;
import persistence.EntityLoader;

import java.util.HashMap;
import java.util.Map;

public class EventListenerRegistry {

    private final Map<EventType, EventListenerGroup> eventTypeEventListenerGroupMap = new HashMap<>();

    public EventListenerRegistry(ActionQueue actionQueue, Metamodel metamodel, EntityLoader entityLoader) {
        eventTypeEventListenerGroupMap.put(EventType.LOAD, new EventListenerGroup(new LoadEventListenerImpl(entityLoader)));
        eventTypeEventListenerGroupMap.put(EventType.PERSIST, new EventListenerGroup(new PersistEventListenerImpl(actionQueue, metamodel)));
        eventTypeEventListenerGroupMap.put(EventType.MERGE, new EventListenerGroup(new MergeEventListenerImpl(actionQueue, metamodel)));
        eventTypeEventListenerGroupMap.put(EventType.DELETE, new EventListenerGroup(new DeleteEventListenerImpl(actionQueue, metamodel)));
    }

    public EventListenerGroup getEventListenerGroup(EventType eventType) {
        return eventTypeEventListenerGroupMap.get(eventType);
    }

}
