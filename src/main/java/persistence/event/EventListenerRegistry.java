package persistence.event;

import persistence.event.delete.DefaultDeleteEventListener;
import persistence.event.delete.DeleteEventListener;
import persistence.event.dirtycheck.DefaultDirtyCheckEventListener;
import persistence.event.dirtycheck.DirtyCheckEventListener;
import persistence.event.load.CacheLoadEventListener;
import persistence.event.load.DefaultLoadEventListener;
import persistence.event.load.LoadEventListener;
import persistence.event.persist.DefaultPersistEventListener;
import persistence.event.persist.OnflushPersistEventListener;
import persistence.event.persist.PersistEventListener;
import persistence.event.update.DefaultUpdateEventListener;
import persistence.event.update.UpdateEventListener;

import java.util.HashMap;
import java.util.Map;

public class EventListenerRegistry {
    private final Map<EventType<?>, EventListenerGroup<?>> eventListenerGroupRegistry = new HashMap<>();

    public EventListenerRegistry() {
        registerLoadEventListener();
        registerPersistEventListener();
        registerPersistOnflushEventListener();
        registerDeleteEventListener();
        registerUpdateEventListener();
        registerDirtyCheckEventListener();
    }

    public <T> EventListenerGroup<T> getEventListenerGroup(EventType<T> eventType) {
        return (EventListenerGroup<T>) eventListenerGroupRegistry.get(eventType);
    }

    private void registerLoadEventListener() {
        final EventListenerGroup<LoadEventListener> eventListenerGroup = new EventListenerGroup<>(EventType.LOAD);
        eventListenerGroup.appendListener(new CacheLoadEventListener());
        eventListenerGroup.appendListener(new DefaultLoadEventListener());
        eventListenerGroupRegistry.put(EventType.LOAD, eventListenerGroup);
    }

    private void registerPersistEventListener() {
        final EventListenerGroup<PersistEventListener> eventListenerGroup = new EventListenerGroup<>(EventType.PERSIST);
        eventListenerGroup.appendListener(new DefaultPersistEventListener());
        eventListenerGroupRegistry.put(EventType.PERSIST, eventListenerGroup);
    }

    private void registerPersistOnflushEventListener() {
        final EventListenerGroup<PersistEventListener> eventListenerGroup = new EventListenerGroup<>(EventType.PERSIST);
        eventListenerGroup.appendListener(new OnflushPersistEventListener());
        eventListenerGroupRegistry.put(EventType.PERSIST_ONFLUSH, eventListenerGroup);
    }

    private void registerDeleteEventListener() {
        final EventListenerGroup<DeleteEventListener> eventListenerGroup = new EventListenerGroup<>(EventType.DELETE);
        eventListenerGroup.appendListener(new DefaultDeleteEventListener());
        eventListenerGroupRegistry.put(EventType.DELETE, eventListenerGroup);
    }

    private void registerUpdateEventListener() {
        final EventListenerGroup<UpdateEventListener> eventListenerGroup = new EventListenerGroup<>(EventType.UPDATE);
        eventListenerGroup.appendListener(new DefaultUpdateEventListener());
        eventListenerGroupRegistry.put(EventType.UPDATE, eventListenerGroup);
    }

    private void registerDirtyCheckEventListener() {
        final EventListenerGroup<DirtyCheckEventListener> eventListenerGroup = new EventListenerGroup<>(EventType.DIRTY_CHECK);
        eventListenerGroup.appendListener(new DefaultDirtyCheckEventListener());
        eventListenerGroupRegistry.put(EventType.DIRTY_CHECK, eventListenerGroup);
    }
}
