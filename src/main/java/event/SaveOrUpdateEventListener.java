package event;

import event.impl.SaveOrUpdateEvent;

public abstract class SaveOrUpdateEventListener<T> implements EventListener<T> {
    @Override
    public void onEvent(Event<T> event) {
        if (event instanceof SaveOrUpdateEvent<T> saveOrUpdateEvent) {
            onSaveOrUpdate(saveOrUpdateEvent);
            return;
        }

        throw new IllegalArgumentException("Event is not an instance of SaveOrUpdateEvent");
    }

    abstract public void onSaveOrUpdate(SaveOrUpdateEvent<T> event);
}
