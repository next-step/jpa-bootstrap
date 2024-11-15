package persistence.event;

public interface UpdateEventListener {
    <T> void onUpdate(UpdateEvent<T> updateEvent);
}
