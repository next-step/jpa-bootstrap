package hibernate.event.delete;

public interface DeleteEventListener {

    <T> void onDelete(DeleteEvent<T> event);
}
