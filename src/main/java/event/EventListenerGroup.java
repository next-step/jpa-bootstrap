package event;

import builder.dml.EntityData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class EventListenerGroup<T> {

    private final List<T> listeners = new ArrayList<>();

    public void handleEvent(EntityData entityData, BiConsumer<T, EntityData> biConsumer) {
        for (T listener : listeners) {
            biConsumer.accept(listener, entityData);
        }
    }

    public <R> R handleEventWithReturn(EntityData entityData, BiFunction<T, EntityData, R> action) {
        for (T listener : listeners) {
            return action.apply(listener, entityData);
        }
        return null;
    }

    public void addListener(T listener) {
        this.listeners.add(listener);
    }

}
