package event.load;

import event.EventListener;

public interface LoadEventListener extends EventListener {

    <T> T onLoad(LoadEvent<T> event);
}
