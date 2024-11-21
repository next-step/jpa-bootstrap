package persistence.event;

public interface EventListener {
    <T> void on(Event<T> event);
}
