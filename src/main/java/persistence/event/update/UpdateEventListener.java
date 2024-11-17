package persistence.event.update;

import persistence.event.EventListener;

public interface UpdateEventListener extends EventListener {
    <T> void onUpdate(UpdateEvent<T> updateEvent);
}
