package persistence.event;

public interface Event<T> {

    Class<T> getTargetClass();
    T getTarget();
    Object getTargetId();
    EventType getType();

}
