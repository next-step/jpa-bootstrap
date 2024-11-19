package event;

import event.impl.LoadEvent;

public interface LoadEventListener extends EventListener {
    <T> void onLoad(LoadEvent<T> event);

}
