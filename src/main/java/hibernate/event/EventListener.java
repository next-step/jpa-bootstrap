package hibernate.event;

import java.util.function.BiFunction;

public class EventListener<T> {

    private final T value;

    public EventListener(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public <E, R> R fireOnLoad(E event, BiFunction<T, E, R> function) {
        return function.apply(value, event);
    }
}
