package event;

import event.impl.DeleteEvent;

public abstract class DeleteEventListener<T> implements EventListener<T> {
    @Override
    public void onEvent(Event<T> event) {
        if (event instanceof DeleteEvent) {
            onDelete((DeleteEvent<T>) event);
            return;
        }

        throw new IllegalArgumentException("Event is not an instance of DeleteEvent");
    }

    abstract public void onDelete(DeleteEvent<T> event);
}
