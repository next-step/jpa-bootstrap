package event.impl;

import event.Event;
import event.EventListener;
import event.EventListenerGroup;
import event.EventType;

import java.util.ArrayList;
import java.util.List;

public class DefaultEventListenerGroup<T extends EventListener> implements EventListenerGroup<T> {
    private final EventType<T> eventType;
    private final List<EventListener> listeners;

    public DefaultEventListenerGroup(EventType<T> eventType) {
        this(eventType, new ArrayList<>());
    }

    public DefaultEventListenerGroup(EventType<T> eventType, List<EventListener> listeners) {
        this.eventType = eventType;
        this.listeners = listeners;
    }

    @Override
    public void addEventListener(EventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void fireEvent(Event event) {
        listeners.forEach(listener -> listener.onEvent(event));
    }

    @Override
    public EventType<T> getEventType() {
        return eventType;
    }
}
