package hibernate.event.delete;

import hibernate.event.EventListener;

public interface DeleteEventListener extends EventListener {

    <T> void onDelete(DeleteEvent<T> event);
}
