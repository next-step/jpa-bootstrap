package event;


import boot.action.ActionQueue;
import boot.metamodel.MetaModel;
import event.delete.DefaultDeleteEventListener;
import event.load.DefaultLoadEventListener;
import event.save.DefaultSaveEventListener;
import event.update.DefaultUpdateEventListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventListenerGroup {

    private final Map<EventType, EventListener<?>> eventListeners;

    private EventListenerGroup(Map<EventType, EventListener<?>> eventListeners) {
        this.eventListeners = eventListeners;
    }

    public static EventListenerGroup createDefaultGroup(MetaModel metaModel, ActionQueue actionQueue) {
        Map<EventType, EventListener<?>> eventListeners = new ConcurrentHashMap<>();
        eventListeners.put(EventType.LOAD, new DefaultLoadEventListener(metaModel));
        eventListeners.put(EventType.SAVE, new DefaultSaveEventListener(metaModel, actionQueue));
        eventListeners.put(EventType.UPDATE, new DefaultUpdateEventListener(metaModel, actionQueue));
        eventListeners.put(EventType.DELETE, new DefaultDeleteEventListener(metaModel, actionQueue));
        return new EventListenerGroup(eventListeners);
    }

    @SuppressWarnings("unchecked")
    public <T> EventListener<T> getListener(EventType eventType) {
        if (!eventListeners.containsKey(eventType)) {
            throw new IllegalArgumentException("EventListener does not exist for the type : " + eventType);
        }
        return (EventListener<T>) eventListeners.get(eventType);
    }
}
