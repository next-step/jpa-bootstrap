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

    private final Map<EventType, EventListenerWrapper<?>> eventListeners;

    private EventListenerGroup(Map<EventType, EventListenerWrapper<?>> eventListeners) {
        this.eventListeners = eventListeners;
    }

    public static EventListenerGroup createDefaultGroup(MetaModel metaModel, ActionQueue actionQueue) {
        Map<EventType, EventListenerWrapper<?>> eventListeners = new ConcurrentHashMap<>();
        eventListeners.put(EventType.LOAD, new EventListenerWrapper<>(new DefaultLoadEventListener(metaModel)));
        eventListeners.put(EventType.SAVE, new EventListenerWrapper<>(new DefaultSaveEventListener(metaModel, actionQueue)));
        eventListeners.put(EventType.UPDATE, new EventListenerWrapper<>(new DefaultUpdateEventListener(metaModel, actionQueue)));
        eventListeners.put(EventType.DELETE, new EventListenerWrapper<>(new DefaultDeleteEventListener(metaModel, actionQueue)));
        return new EventListenerGroup(eventListeners);
    }

    @SuppressWarnings("unchecked")
    public <T> EventListenerWrapper<T> getListener(EventType eventType) {
        if (!eventListeners.containsKey(eventType)) {
            throw new IllegalArgumentException("EventListener does not exist for the type : " + eventType);
        }
        return (EventListenerWrapper<T>) eventListeners.get(eventType);
    }
}
