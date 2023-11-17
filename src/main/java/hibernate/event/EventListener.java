package hibernate.event;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class EventListener<T> {

    private final T value;

    public EventListener(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public <E, R> R fireWithReturn(E event, BiFunction<T, E, R> action) {
        return action.apply(value, event);
    }

    public <E> void fireJustRun(E event, BiConsumer<T, E> action) {
        action.accept(value, event);
    }
}