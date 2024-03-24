package persistence.entity.event.load;

import persistence.entity.event.EventListener;

public interface LoadEventListener extends EventListener {

    <ID, T> T onLoad(LoadEvent<ID> event);
}
