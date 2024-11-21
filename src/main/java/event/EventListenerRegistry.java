package event;

import event.impl.DeleteEvent;
import event.impl.LoadEvent;
import event.impl.SaveOrUpdateEvent;

public interface EventListenerRegistry {
    void addEventListenerGroup(EventType eventType, EventListenerGroup<?> listenerGroup);

    <T extends EventListener<?>> EventListenerGroup<T> getEventListenerGroup(EventType eventType);

    <T> void fireEventOnEachListener(SaveOrUpdateEvent<T> event);
    <T> void fireEventOnEachListener(LoadEvent<T> event);
    <T> void fireEventOnEachListener(DeleteEvent<T> event);
}
