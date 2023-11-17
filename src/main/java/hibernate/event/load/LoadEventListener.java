package hibernate.event.load;

import hibernate.event.EventListener;

public interface LoadEventListener extends EventListener {

    <T> T onLoad(LoadEvent<T> event);
}
