package hibernate.event;

public class EventListener<T> {

    private final T value;

    public EventListener(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
