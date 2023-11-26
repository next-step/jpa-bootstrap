package persistence.entity.impl.event.listener;

import persistence.entity.EntityEntry;
import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;
import persistence.entity.impl.event.EntityEventListener;
import persistence.entity.impl.store.EntityPersister;
import persistence.sql.schema.meta.EntityObjectMappingMeta;

public class MergeEntityEventListenerImpl implements EntityEventListener {

    private final EntityPersister entityPersister;

    public MergeEntityEventListenerImpl(EntityPersister entityPersister) {
        this.entityPersister = entityPersister;
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        throw new RuntimeException("MergeEvent는 반환이 없는 이벤트를 지원하지 않습니다.");
    }

    @Override
    public <T> T onEvent(Class<T> clazz, EntityEvent entityEvent) {
        final Object entity = entityEvent.getEntity();

        final EventSource eventSource = entityEvent.getEventSource();
        final EntityEntry entityEntry = eventSource.getEntityEntry(entity);
        if (entityEntry.isReadOnly()) {
            throw new RuntimeException("해당 Entity는 변경될 수 없습니다.");
        }

        entityPersister.update(entity);
        syncPersistenceContext(eventSource, entity);
        return clazz.cast(entity);
    }

    @Override
    public void syncPersistenceContext(EventSource eventSource, Object entity) {
        eventSource.putEntity(entity);
    }
}
