package event;

public interface Event {
    Object getEntity();
    String getEntityName();
    Object getEntityId();
}
