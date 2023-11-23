package persistence.event;

public interface EventListener {
    <T> T on(Event<T> event);

}
