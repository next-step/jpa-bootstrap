package persistence.event;


import persistence.action.ActionQueue;
import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersisters;
import persistence.exception.PersistenceException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventDispatcher {

    private final Map<EventType, EventListener> listeners;

    public EventDispatcher(final ActionQueue actionQueue, final EntityPersisters entityPersisters, final EntityLoaders entityLoaders) {
        this.listeners = new HashMap<>();
        listeners.put(EventType.PERSIST, new PersistEventListener(actionQueue, entityPersisters));
        listeners.put(EventType.MERGE, new MergeEventListener(actionQueue, entityPersisters));
        listeners.put(EventType.DELETE, new DeleteEventListener(actionQueue, entityPersisters));
        listeners.put(EventType.LOAD, new LoadEventListener(entityLoaders));
    }

    public  <T> T dispatch(final Event<T> event) {
        final EventListener eventListener = listeners.get(event.getType());
        if (Objects.isNull(eventListener)) {
            throw new PersistenceException("존재하지 않는 Event Type 입니다.");
        }
        return eventListener.on(event);
    }

}
