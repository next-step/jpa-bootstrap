package event.delete;

import event.EventListener;

public interface DeleteEventListener extends EventListener {

    <T> void onDelete(DeleteEvent<T> event);
}
