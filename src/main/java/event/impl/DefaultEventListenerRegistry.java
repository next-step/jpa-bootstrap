package event.impl;

import event.DeleteEventListener;
import event.EventListener;
import event.EventListenerGroup;
import event.EventListenerRegistry;
import event.EventType;
import event.LoadEventListener;
import event.SaveOrUpdateEventListener;

import java.util.EnumMap;

public class DefaultEventListenerRegistry implements EventListenerRegistry {
    private final EnumMap<EventType, EventListenerGroup<?>> listenerGroups = new EnumMap<>(EventType.class);

    @Override
    public void addEventListenerGroup(EventType eventType, EventListenerGroup<?> listenerGroup) {
        listenerGroups.put(eventType, listenerGroup);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EventListener<?>> EventListenerGroup<T> getEventListenerGroup(EventType eventType) {
        EventListenerGroup<?> eventListenerGroup = listenerGroups.computeIfAbsent(eventType, k -> new DefaultEventListenerGroup<>(eventType));

        if (eventListenerGroup.getEventType() == eventType) {
            return (EventListenerGroup<T>) eventListenerGroup;
        }

        throw new IllegalArgumentException("Incorrect event type");
    }

    @Override
    public <T> void fireEventOnEachListener(SaveOrUpdateEvent<T> event) {
        EventListenerGroup<SaveOrUpdateEventListener<T>> eventListenerGroup = getEventListenerGroup(EventType.SAVE_OR_UPDATE);
        eventListenerGroup.fireEventOnEachListener(event, SaveOrUpdateEventListener::onSaveOrUpdate);
    }

    @Override
    public <T> void fireEventOnEachListener(LoadEvent<T> event) {
        EventListenerGroup<LoadEventListener<T>> eventListenerGroup = getEventListenerGroup(EventType.LOAD);
        eventListenerGroup.fireEventOnEachListener(event, LoadEventListener::onLoad);
    }

    @Override
    public <T> void fireEventOnEachListener(DeleteEvent<T> event) {
        EventListenerGroup<DeleteEventListener<T>> eventListenerGroup = getEventListenerGroup(EventType.DELETE);
        eventListenerGroup.fireEventOnEachListener(event, DeleteEventListener::onDelete);
    }
}
