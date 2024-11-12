package persistence.event;

public interface DeleteEventListener {

    void onDelete(DeleteEvent event);
}
