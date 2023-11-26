package persistence.entity.impl.event.listener;

import persistence.entity.EntityEntry;
import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;
import persistence.entity.impl.event.EntityEventListener;
import persistence.entity.impl.store.EntityPersister;

public class DeleteEntityEventListenerImpl implements EntityEventListener {

    private final EntityPersister entityPersister;

    public DeleteEntityEventListenerImpl(EntityPersister entityPersister) {
        this.entityPersister = entityPersister;
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        final Object entity = entityEvent.getEntity();
        final EventSource eventSource = entityEvent.getEventSource();

        final EntityEntry entityEntry = eventSource.getEntityEntry(entity);
        if (entityEntry.isReadOnly()) {
            throw new RuntimeException("해당 Entity는 삭제될 수 없습니다.");
        }

        entityPersister.delete(entity);

        eventSource.deleted(entity);

        syncPersistenceContext(eventSource, entity);
    }

    @Override
    public <T> T onEvent(Class<T> clazz, EntityEvent entityEvent) {
        throw new RuntimeException("DeleteEventListener는 반환이 있는 이벤트를 지원하지 않습니다.");
    }

    @Override
    public void syncPersistenceContext(EventSource eventSource, Object entity) {
        eventSource.purgeEntity(entity);
    }
}
