package persistence.event;

public interface DeleteEventListener extends EventListener {
    void onDelete(final DeleteEvent deleteEvent);
}
