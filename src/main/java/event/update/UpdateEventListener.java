package event.update;

import event.EventListener;

public interface UpdateEventListener extends EventListener {

    <T> void onUpdate(UpdateEvent<T> event);
}
