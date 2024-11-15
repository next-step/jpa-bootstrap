package persistence.event;

public interface DeleteEventListener {
    <T> void onDelete(DeleteEvent<T> deleteEvent);
}
