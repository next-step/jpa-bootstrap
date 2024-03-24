package persistence.entity.event;

import bootstrap.MetaModel;
import persistence.entity.event.action.ActionQueue;
import persistence.entity.event.delete.DeleteEventListener;
import persistence.entity.event.load.LoadEvent;
import persistence.entity.event.load.LoadEventListener;
import persistence.entity.event.save.SaveEventListener;
import persistence.entity.event.update.UpdateEventListener;

public class FastEventSessionService {
    private final EventListenerGroup<LoadEventListener> loadEventListeners;
    private final EventListenerGroup<SaveEventListener> saveEventListeners;
    private final EventListenerGroup<UpdateEventListener> updateEventListeners;
    private final EventListenerGroup<DeleteEventListener> deleteEventListeners;

    public FastEventSessionService(EventListenerGroup<LoadEventListener> loadEventListeners, EventListenerGroup<SaveEventListener> saveEventListeners, EventListenerGroup<UpdateEventListener> updateEventListeners, EventListenerGroup<DeleteEventListener> deleteEventListeners) {
        this.loadEventListeners = loadEventListeners;
        this.saveEventListeners = saveEventListeners;
        this.updateEventListeners = updateEventListeners;
        this.deleteEventListeners = deleteEventListeners;
    }

    public static FastEventSessionService create(MetaModel metaModel, ActionQueue actionQueue) {

        EventListenerRegistry eventListenerRegistry = EventListenerRegistry.create(metaModel, actionQueue);
        return new FastEventSessionService(eventListenerRegistry.getEventListenerGroup(EventType.LOAD),
                eventListenerRegistry.getEventListenerGroup(EventType.SAVE),
                eventListenerRegistry.getEventListenerGroup(EventType.UPDATE),
                eventListenerRegistry.getEventListenerGroup(EventType.DELETE));
    }

}
