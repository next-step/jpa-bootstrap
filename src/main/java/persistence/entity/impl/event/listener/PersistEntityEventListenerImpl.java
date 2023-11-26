package persistence.entity.impl.event.listener;

import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;
import persistence.entity.impl.event.EntityEventListener;
import persistence.entity.impl.store.EntityPersister;

public class PersistEntityEventListenerImpl implements EntityEventListener {

    private final EntityPersister entityPersister;

    public PersistEntityEventListenerImpl(EntityPersister entityPersister) {
        this.entityPersister = entityPersister;
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        throw new RuntimeException("PersistEventListener는 반환값이 있는 이벤트만 처리할 수 있습니다.");
    }

    @Override
    public <T> T onEvent(Class<T> clazz, EntityEvent entityEvent) {
        final Object savedEntity = entityPersister.store(entityEvent.getEntity());
        final EventSource eventSource = entityEvent.getEventSource();
        eventSource.saving(savedEntity);

        syncPersistenceContext(eventSource, savedEntity);
        return clazz.cast(savedEntity);
    }

    @Override
    public void syncPersistenceContext(EventSource eventSource, Object entity) {
        eventSource.putEntity(entity);
    }
}
