package persistence.event;

import java.util.List;

public final class EventListenerGroupHandler {
    public final EventListenerGroup<PersistEventListener> PERSIST;
    public final EventListenerGroup<LoadEventListener> LOAD;
    public final EventListenerGroup<MergeEventListener> MERGE;
    public final EventListenerGroup<DeleteEventListener> DELETE;

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

        MERGE = new EventListenerGroupImpl<>(
                EventType.MERGE,
                List.of(
                        new DefaultMergeEventListener()
                )
        );

        DELETE = new EventListenerGroupImpl<>(
                EventType.DELETE,
                List.of(
                        new DefaultDeleteEventListener()
                )
        );
    }
}
