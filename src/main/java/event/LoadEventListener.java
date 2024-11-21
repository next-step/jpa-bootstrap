package event;

import event.impl.LoadEvent;

public abstract class LoadEventListener<T> implements EventListener<T> {

    @Override
    public void onEvent(Event<T> event) {
        if (event instanceof LoadEvent<T> loadEvent) {
            onLoad(loadEvent);
            return;
        }

        throw new IllegalArgumentException("Event is not an instance of LoadEvent");
    }

    abstract public void onLoad(LoadEvent<T> event);
}
