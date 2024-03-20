package event.save;

import event.EventListener;

public interface SaveEventListener extends EventListener {

    <T> void onSave(SaveEvent<T> event);
}
