package persistence.event;

import java.util.List;

public final class EventListenerGroupHandler {
    public final EventListenerGroup<PersistEventListener> PERSIST;

    public EventListenerGroupHandler() {
        PERSIST = new EventListenerGroupImpl<>(
                EventType.PERSIST,
                List.of(
                        new DefaultPersistEventListener(),
                        new CollectionPersistEventListener()
                )
        );
    }
}
