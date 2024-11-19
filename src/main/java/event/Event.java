package event;

public interface Event {
    Object entity();
    String entityName();
    Object entityId();
}
