package persistence.event;

import java.util.Collection;
import java.util.function.BiConsumer;

public interface EventListenerGroup<T> {

    EventType<T> getEventType();

    Collection<T> getListeners();

    <U> void fireEventOnEachListener(final U event, final BiConsumer<T, U> actionOnEvent);
}
