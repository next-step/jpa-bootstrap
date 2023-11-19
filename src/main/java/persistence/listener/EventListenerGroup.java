package persistence.listener;

import java.util.function.Function;

public interface EventListenerGroup<T> {
    <U, X> void fireEventOnEachListener(final U event, X param, final EventActionWithParameter<T, U, X> actionOnEvent);

    <U, R> R fireEventOnEachListener(U event, Function<T, R> actionOnEvent);

    void addListener(T listener);
}
