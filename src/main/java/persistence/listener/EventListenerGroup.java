package persistence.listener;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface EventListenerGroup<T> {
    <U> void fireEventOnEachListener(final U event, final BiConsumer<T, U> actionOnEvent);

    <U, R> R fireEventOnEachListener(U event, Function<T, R> actionOnEvent);

    void addListener(T listener);
}
