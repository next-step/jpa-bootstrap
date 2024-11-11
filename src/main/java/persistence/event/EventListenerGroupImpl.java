package persistence.event;

import java.util.List;
import java.util.function.BiConsumer;

public class EventListenerGroupImpl<T> implements EventListenerGroup<T> {

    private final EventType<T> eventType;
    private final List<T> listeners;

    public EventListenerGroupImpl(EventType<T> eventType, List<T> listeners) {
        this.eventType = eventType;
        this.listeners = listeners;
    }

    @Override
    public EventType<T> getEventType() {
        return eventType;
    }

    @Override
    public <U> void fireEventOnEachListener(U event, BiConsumer<T, U> actionOnEvent) {
        listeners.forEach(listener -> actionOnEvent.accept(listener, event));
    }

}
