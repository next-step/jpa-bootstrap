package event;

import event.impl.DeleteEvent;

public interface DeleteEventListener extends EventListener{
    void onDelete(DeleteEvent event);
}
