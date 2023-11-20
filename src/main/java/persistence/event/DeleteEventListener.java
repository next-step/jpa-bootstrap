package persistence.event;

public interface DeleteEventListener {
    void onDelete(final DeleteEvent deleteEvent);
}
