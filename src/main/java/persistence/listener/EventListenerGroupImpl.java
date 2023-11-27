package persistence.listener;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class EventListenerGroupImpl<T> implements EventListenerGroup<T> {

    private T[] listeners;

    @Override
    public <U> void fireEventOnEachListener(final U event, final BiConsumer<T, U> actionOnEvent) {
        final T[] ls = listeners;
        if (ls != null) {
            for (T listener : ls) {
                actionOnEvent.accept(listener, event);
            }
        }
    }

    @Override
    public <U, R> R fireEventOnEachListener(U event, Function<T, R> actionOnEvent) {
        for (T listener : listeners) {
            R result = actionOnEvent.apply(listener);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public void addListener(T listener) {
        if (listeners == null) {
            listeners = createListenersArray(1);
            listeners[0] = listener;
        } else {
            T[] newListeners = createListenersArray(listeners.length + 1);
            System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
            newListeners[listeners.length] = listener;
            listeners = newListeners;
        }
    }

    @SuppressWarnings("unchecked")
    private T[] createListenersArray(int size) {
        return (T[]) new Object[size];
    }
}
