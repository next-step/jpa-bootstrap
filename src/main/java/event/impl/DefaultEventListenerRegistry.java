package event.impl;

import event.EventListener;
import event.EventListenerGroup;
import event.EventListenerRegistry;
import event.EventType;

import java.util.HashMap;
import java.util.Map;

public class DefaultEventListenerRegistry implements EventListenerRegistry {
    private static final Map<EventType<? extends EventListener>, EventListenerGroup> listenerGroups = new HashMap<>();

    @Override
    public void addEventListenerGroup(EventType<? extends EventListener> eventType, EventListenerGroup listenerGroup) {
        listenerGroups.put(eventType, listenerGroup);
    }

    @Override
    public EventListenerGroup getEventListenerGroup(EventType<? extends EventListener> eventType) {
        return listenerGroups.get(eventType);
    }
}
