package persistence.event;

import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersisters;

import java.util.Map;

public class EventListenerRegistry {
    private final Map<EventType<?>, EventListenerGroup<?>> listeners;

    public EventListenerRegistry(final EntityPersisters entityPersisters, final EntityLoaders entityLoaders) {
        this.listeners = Map.of(
                EventType.PERSIST, new EventListenerGroup<>(new DefaultPersisEventListener(entityPersisters)),
                EventType.MERGE, new EventListenerGroup<>(new DefaultMergeEventListener(entityPersisters)),
                EventType.DELETE, new EventListenerGroup<>(new DefaultDeleteEventListener(entityPersisters)),
                EventType.LOAD, new EventListenerGroup<>(new DefaultLoadEventListener(entityLoaders))
        );
    }

    @SuppressWarnings("unchecked")
    public <T extends EventListener> EventListenerGroup<T> getListener(final EventType<T> type) {
        return (EventListenerGroup<T>) listeners.get(type);
    }
}
