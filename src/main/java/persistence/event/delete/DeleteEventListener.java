package persistence.event.delete;

public interface DeleteEventListener {

    void onDelete(DeleteEvent event);
}
