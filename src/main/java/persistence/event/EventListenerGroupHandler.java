package persistence.event;

import java.util.List;

public final class EventListenerGroupHandler {
    public final EventListenerGroup<PersistEventListener> PERSIST;
    public final EventListenerGroup<LoadEventListener> LOAD;

    public EventListenerGroupHandler() {
        PERSIST = new EventListenerGroupImpl<>(
                EventType.PERSIST,
                List.of(
                        new DefaultPersistEventListener(),
                        new CollectionPersistEventListener()
                )
        );

        LOAD = new EventListenerGroupImpl<>(
                EventType.LOAD,
                List.of(
                        new DefaultLoadEventListener()
                )
        );
    }
}
