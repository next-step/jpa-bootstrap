package persistence.event;

import persistence.event.delete.DefaultDeleteEventListener;
import persistence.event.delete.DeleteEventListener;
import persistence.event.flush.DefaultFlushEventListener;
import persistence.event.flush.FlushEventListener;
import persistence.event.load.DefaultLoadEventListener;
import persistence.event.load.LoadEventListener;
import persistence.event.merge.DefaultMergeEventListener;
import persistence.event.merge.MergeEventListener;
import persistence.event.persist.CollectionPersistEventListener;
import persistence.event.persist.DefaultPersistEventListener;
import persistence.event.persist.PersistEventListener;

import java.util.List;

public final class EventListenerGroupHandler {
    public final EventListenerGroup<PersistEventListener> PERSIST;
    public final EventListenerGroup<LoadEventListener> LOAD;
    public final EventListenerGroup<MergeEventListener> MERGE;
    public final EventListenerGroup<DeleteEventListener> DELETE;
    public final EventListenerGroup<FlushEventListener> FLUSH;

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

        FLUSH = new EventListenerGroupImpl<>(
                EventType.FLUSH,
                List.of(
                        new DefaultFlushEventListener()
                )
        );
    }
}
