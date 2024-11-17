package persistence.event.delete;

import persistence.event.EventListener;

public interface DeleteEventListener extends EventListener {
    <T> void onDelete(DeleteEvent<T> deleteEvent);
}
