package persistence.entity.manager;

import persistence.context.PersistenceContext;
import persistence.entity.FastSessionServices;
import persistence.listener.*;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final FastSessionServices fastSessionServices;

    private EntityManagerImpl(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
        this.fastSessionServices = new FastSessionServices();
        initializeEventListeners();
    }

    public static EntityManagerImpl of(PersistenceContext persistenceContext) {
        return new EntityManagerImpl(persistenceContext);
    }

    @Override
    public <T> T findById(Class<T> clazz, String id) {
        LoadEvent loadEvent = new LoadEvent(clazz, id);
        fastSessionServices.eventListenerGroup_LOAD.fireEventOnEachListener(loadEvent, LoadEventListener::onLoad);
        return (T) loadEvent.getLoadedEntity();
    }

    @Override
    public <T> T persist(T instance) {
        PersistEvent<T> persistEvent = new PersistEvent<>(instance);
        return fastSessionServices.eventListenerGroup_PERSIST.fireEventOnEachListener(persistEvent, listener -> listener.onPersist(persistEvent));
    }

    @Override
    public <T> void remove(T instance) {
        persistenceContext.removeEntity(instance);
    }

    @Override
    public <T> void flush() {
        persistenceContext.flush();
    }

    @Override
    public PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }


    private void initializeEventListeners() {
        LoadEventListener loadListener = new LoadEventListenerImpl(this);
        PersistEventListener persistEventListener = new PersistEventListenerImpl(this);

        fastSessionServices.eventListenerGroup_LOAD.addListener(loadListener);
        fastSessionServices.eventListenerGroup_PERSIST.addListener(persistEventListener);
    }
}
