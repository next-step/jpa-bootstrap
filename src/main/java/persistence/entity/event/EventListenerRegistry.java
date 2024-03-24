package persistence.entity.event;

import bootstrap.MetaModel;
import persistence.entity.event.action.ActionQueue;
import persistence.entity.event.delete.DeleteEventListener;
import persistence.entity.event.load.DefaultLoadEventListener;
import persistence.entity.event.save.SaveEventListener;
import persistence.entity.event.update.UpdateEventListener;
import persistence.exception.HibernateException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventListenerRegistry {

    private final List<EventListenerGroup<?>> eventListenerGroups;

    private EventListenerRegistry(List<EventListenerGroup<?>> eventListenerGroups) {
        this.eventListenerGroups = new ArrayList<>(eventListenerGroups);
    }

    public static EventListenerRegistry create(MetaModel metaModel, ActionQueue actionQueue) {
        List<EventListenerGroup<?>> listenerGroups = List.of(
                new EventListenerGroup<>(
                        EventType.LOAD, new DefaultLoadEventListener(metaModel)
                ),
                new EventListenerGroup<>(
                        EventType.SAVE, new SaveEventListener(metaModel, actionQueue)
                ),
                new EventListenerGroup<>(
                        EventType.UPDATE, new UpdateEventListener(metaModel, actionQueue)
                ),
                new EventListenerGroup<>(
                        EventType.DELETE, new DeleteEventListener(metaModel, actionQueue)
                )
        );
        return new EventListenerRegistry(listenerGroups);
    }

    public <T extends EventListener> EventListenerGroup<T> getEventListenerGroup(EventType eventType) {
        validateEventListenerGroupSize(eventType);

        EventListenerGroup<T> listeners = (EventListenerGroup<T>) this.eventListenerGroups.get(eventType.ordinal());
        if (listeners == null) {
            throw new HibernateException("Unable to find listeners for type [" + eventType.getEventName() + "]");
        }
        return listeners;
    }

    private void validateEventListenerGroupSize(EventType eventType) {
        if (this.eventListenerGroups.size() < eventType.ordinal() + 1) {
            throw new HibernateException("Invalid listeners for type [" + eventType.getEventName() + "]");
        }
    }
}
