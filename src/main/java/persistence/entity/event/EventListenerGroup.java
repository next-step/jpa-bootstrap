package persistence.entity.event;

import bootstrap.MetaModel;
import persistence.entity.event.action.ActionQueue;
import persistence.entity.event.delete.DeleteEventListener;
import persistence.entity.event.load.DefaultLoadEventListener;
import persistence.entity.event.load.LoadEventListener;
import persistence.entity.event.save.SaveEventListener;
import persistence.entity.event.update.UpdateEventListener;

import java.util.Map;

public class EventListenerGroup {

    private final Map<EventType, LoadEventListener> loadEventListenerMap;
    private final Map<EventType, PersistEventListener> persistEventListenerMap;

    public EventListenerGroup(Map<EventType, LoadEventListener> loadEventListenerMap, Map<EventType, PersistEventListener> persistEventListenerMap) {
        this.loadEventListenerMap = loadEventListenerMap;
        this.persistEventListenerMap = persistEventListenerMap;
    }

    public static EventListenerGroup create(MetaModel metaModel, ActionQueue actionQueue) {

        return new EventListenerGroup(Map.of(
                EventType.LOAD, new DefaultLoadEventListener(metaModel)),
                Map.of(
                        EventType.SAVE, new SaveEventListener(metaModel, actionQueue),
                        EventType.UPDATE, new UpdateEventListener(metaModel, actionQueue),
                        EventType.DELETE, new DeleteEventListener(metaModel, actionQueue)
                )
        );
    }

    public LoadEventListener getLoadEventListener(EventType eventType) {
        return loadEventListenerMap.get(eventType);
    }

    public PersistEventListener getPersistEventListener(EventType eventType) {
        return persistEventListenerMap.get(eventType);
    }

}
