package hibernate.event;

import hibernate.event.delete.SimpleDeleteEventListener;
import hibernate.event.load.SimpleLoadEventListener;
import hibernate.event.merge.SimpleMergeEventListener;
import hibernate.event.persist.SimplePersistEventListener;
import hibernate.metamodel.MetaModel;

import java.util.Map;

public class EventListenerRegistry {

    private final Map<EventType, EventListener<?>> listeners;

    public EventListenerRegistry(final Map<EventType, EventListener<?>> listeners) {
        this.listeners = listeners;
    }

    public static EventListenerRegistry createDefaultRegistry(final MetaModel metaModel) {
        return new EventListenerRegistry(Map.of(
                EventType.LOAD, new EventListener<>(new SimpleLoadEventListener(metaModel)),
                EventType.PERSIST, new EventListener<>(new SimplePersistEventListener(metaModel)),
                EventType.MERGE, new EventListener<>(new SimpleMergeEventListener(metaModel)),
                EventType.DELETE, new EventListener<>(new SimpleDeleteEventListener(metaModel))
        ));
    }

    public <T> EventListener<T> getListener(final EventType eventType) {
        if (listeners.containsKey(eventType)) {
            return (EventListener<T>) listeners.get(eventType);
        }
        throw new IllegalArgumentException("해당 EventType에 일치하는 EventListener가 없습니다.");
    }
}
