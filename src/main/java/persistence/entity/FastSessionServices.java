package persistence.entity;

import persistence.listener.EventListenerGroup;
import persistence.listener.EventListenerGroupImpl;
import persistence.listener.LoadEventListener;
import persistence.listener.PersistEventListener;

public class FastSessionServices {
    public final EventListenerGroup<LoadEventListener> eventListenerGroup_LOAD;
    public final EventListenerGroup<PersistEventListener> eventListenerGroup_PERSIST;

    public FastSessionServices() {
        this.eventListenerGroup_LOAD = new EventListenerGroupImpl<>();
        this.eventListenerGroup_PERSIST = new EventListenerGroupImpl<>();
    }
}
