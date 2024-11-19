package event;

import event.impl.LoadEvent;

public interface LoadEventListener extends EventListener{
    <T> T onLoad(LoadEvent event);

}
