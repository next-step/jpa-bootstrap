package event;

import event.impl.SaveOrUpdateEvent;

public interface SaveOrUpdateEventListener extends EventListener {
    void onSaveOrUpdate(SaveOrUpdateEvent event);
}
