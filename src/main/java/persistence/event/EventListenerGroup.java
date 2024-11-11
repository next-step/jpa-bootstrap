package persistence.event;

import java.util.function.BiConsumer;

public interface EventListenerGroup<T> {

    EventType<T> getEventType();

    <U> void fireEventOnEachListener(final U event, final BiConsumer<T, U> actionOnEvent);

}
