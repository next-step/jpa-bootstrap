package event;

import boot.Metamodel;
import event.action.ActionQueue;
import event.listener.delete.DeleteEventListener;
import event.listener.delete.DeleteEventListenerImpl;
import event.listener.load.LoadEventListener;
import event.listener.load.LoadEventListenerImpl;
import event.listener.merge.MergeEventListener;
import event.listener.merge.MergeEventListenerImpl;
import event.listener.persist.PersistEventListener;
import event.listener.persist.PersistEventListenerImpl;
import persistence.EntityLoader;

import java.util.HashMap;
import java.util.Map;

public class EventListenerRegistry {

    private final Map<EventType,EventListenerGroup<?>> eventListenerGroupMap = new HashMap<>();

    public EventListenerRegistry(Metamodel metamodel, EntityLoader entityLoader, ActionQueue actionQueue) {
        this.eventListenerGroupMap.put(EventType.LOAD, createLoadEventListenerGroup(entityLoader));
        this.eventListenerGroupMap.put(EventType.PERSIST, createPersistEventListenerGroup(metamodel, actionQueue));
        this.eventListenerGroupMap.put(EventType.MERGE, createMergeEventListenerGroup(metamodel, actionQueue));
        this.eventListenerGroupMap.put(EventType.DELETE, createDeleteEventListenerGroup(metamodel, actionQueue));
    }

    public static EventListenerRegistry createEventListenerRegistry(Metamodel metamodel, EntityLoader entityLoader, ActionQueue actionQueue) {
        return new EventListenerRegistry(metamodel, entityLoader, actionQueue);
    }

    public EventListenerGroup<?> getEventListenerGroup(EventType eventType) {
        return eventListenerGroupMap.get(eventType);
    }

    public EventListenerGroup<LoadEventListener<?>> createLoadEventListenerGroup(EntityLoader entityLoader) {
        EventListenerGroup<LoadEventListener<?>> eventListenerGroup = new EventListenerGroup<>();
        eventListenerGroup.addListener(new LoadEventListenerImpl<>(entityLoader));
        return eventListenerGroup;
    }

    public EventListenerGroup<PersistEventListener> createPersistEventListenerGroup(Metamodel metamodel, ActionQueue actionQueue) {
        EventListenerGroup<PersistEventListener> eventListenerGroup = new EventListenerGroup<>();
        eventListenerGroup.addListener(new PersistEventListenerImpl(metamodel, actionQueue));
        return eventListenerGroup;
    }

    public EventListenerGroup<MergeEventListener> createMergeEventListenerGroup(Metamodel metamodel, ActionQueue actionQueue) {
        EventListenerGroup<MergeEventListener> eventListenerGroup = new EventListenerGroup<>();
        eventListenerGroup.addListener(new MergeEventListenerImpl(metamodel, actionQueue));
        return eventListenerGroup;
    }

    public EventListenerGroup<DeleteEventListener> createDeleteEventListenerGroup(Metamodel metamodel, ActionQueue actionQueue) {
        EventListenerGroup<DeleteEventListener> eventListenerGroup = new EventListenerGroup<>();
        eventListenerGroup.addListener(new DeleteEventListenerImpl(metamodel, actionQueue));
        return eventListenerGroup;
    }

    @SuppressWarnings("unchecked")
    public <T> void addListener(EventType eventType, T listener) {
        if (!eventType.getClazz().isInstance(listener)) {
            throw new IllegalArgumentException("Listener의 타입이 맞지 않습니다." + eventType.getClazz());
        }
        EventListenerGroup<T> eventListenerGroup = (EventListenerGroup<T>) this.eventListenerGroupMap.get(eventType);
        eventListenerGroup.addListener(listener);
    }

}
