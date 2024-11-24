package event.impl;

import event.EventListenerGroup;
import event.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class DefaultEventListenerGroup<T> implements EventListenerGroup<T> {
    private final EventType eventType;
    private final List<T> listeners;

    public DefaultEventListenerGroup(EventType eventType) {
        this(eventType, new ArrayList<>());
    }

    public DefaultEventListenerGroup(EventType eventType, List<T> listeners) {
        this.eventType = eventType;
        this.listeners = listeners;
    }

    @Override
    public void addEventListener(T listener) {
        listeners.add(listener);
    }

    @Override
    public <U> void fireEventOnEachListener(U event, BiConsumer<T, U> actionOnEvent) {
        for (T listener : listeners) {
            actionOnEvent.accept(listener, event);
        }
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }
}
