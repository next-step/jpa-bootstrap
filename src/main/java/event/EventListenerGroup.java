package event;

import java.util.function.BiConsumer;

public interface EventListenerGroup<T> {

    void addEventListener(T listener);

    <U> void fireEventOnEachListener(U event, BiConsumer<T, U> actionOnEvent);

    EventType getEventType();
}
