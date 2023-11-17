package hibernate.event;

import hibernate.event.delete.SimpleDeleteEventListener;
import hibernate.event.load.SimpleLoadEventListener;
import hibernate.event.merge.SimpleMergeEventListener;
import hibernate.event.persist.SimplePersistEventListener;

import java.util.Map;

public class EventListenerRegistry {

    private final Map<EventType, EventListener> listeners;

    public EventListenerRegistry(final Map<EventType, EventListener> listeners) {
        this.listeners = listeners;
    }

    public static EventListenerRegistry createDefaultRegistry() {
        return new EventListenerRegistry(Map.of(
                EventType.LOAD, new SimpleLoadEventListener(),
                EventType.PERSIST, new SimplePersistEventListener(),
                EventType.MERGE, new SimpleMergeEventListener(),
                EventType.DELETE, new SimpleDeleteEventListener()
        ));
    }

    public EventListener getListener(final EventType eventType) {
        if (listeners.containsKey(eventType)) {
            return listeners.get(eventType);
        }
        throw new IllegalArgumentException("해당 EventType에 일치하는 EventListener가 없습니다.");
    }
}
