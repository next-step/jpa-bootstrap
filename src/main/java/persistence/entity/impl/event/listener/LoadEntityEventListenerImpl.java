package persistence.entity.impl.event.listener;

import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;
import persistence.entity.impl.event.EntityEventListener;
import persistence.entity.impl.retrieve.EntityLoader;

public class LoadEntityEventListenerImpl implements EntityEventListener {

    private final EntityLoader entityLoader;

    public LoadEntityEventListenerImpl(EntityLoader entityLoader) {
        this.entityLoader = entityLoader;
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        throw new RuntimeException("LoadEvent는 반환값이 항상 존재합니다.");
    }

    @Override
    public <T> T onEvent(Class<T> clazz, EntityEvent entityEvent) {
        final T loadedEntity = entityLoader.load(clazz, entityEvent.getId());
        final EventSource eventSource = entityEvent.getEventSource();
        eventSource.loading(loadedEntity);

        syncPersistenceContext(eventSource, loadedEntity);
        return loadedEntity;
    }

    @Override
    public void syncPersistenceContext(EventSource eventSource, Object entity) {
        eventSource.putEntity(entity);
    }
}
