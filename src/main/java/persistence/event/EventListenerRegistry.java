package persistence.event;

import persistence.action.ActionQueue;
import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersisters;

import java.util.Map;

public class EventListenerRegistry {
    private final Map<EventType<?>, EventListenerGroup<?>> listeners;

    public EventListenerRegistry(final ActionQueue actionQueue, final EntityPersisters entityPersisters, final EntityLoaders entityLoaders) {
        this.listeners = Map.of(
                EventType.PERSIST, new EventListenerGroup<>(new DefaultPersisEventListener(actionQueue, entityPersisters)),
                EventType.MERGE, new EventListenerGroup<>(new DefaultMergeEventListener(actionQueue, entityPersisters)),
                EventType.DELETE, new EventListenerGroup<>(new DefaultDeleteEventListener(actionQueue, entityPersisters)),
                EventType.LOAD, new EventListenerGroup<>(new DefaultLoadEventListener(entityLoaders))
        );
    }

    @SuppressWarnings("unchecked")
    public <T extends EventListener> EventListenerGroup<T> getListener(final EventType<T> type) {
        return (EventListenerGroup<T>) listeners.get(type);
    }
}
