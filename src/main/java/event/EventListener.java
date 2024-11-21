package event;

public interface EventListener<T> {
    void onEvent(Event<T> event);
}
