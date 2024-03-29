package persistence.entitymanager.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class EventListenerGroup<T> {
    private final List<T> listeners;

    public EventListenerGroup() {
        this.listeners = new ArrayList<>();
    }

    public void registerListener(T listener) {
        listeners.add(listener);
    }

    public <U> void fireEventOnEachListener(final U event, final BiConsumer<T, U> consumer) {
        for (T listener : listeners) {
            consumer.accept(listener, event);
        }
    }
}
