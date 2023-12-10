package persistence.entity.impl.event.listener;

import persistence.entity.EntityEntry;
import persistence.entity.ContextSource;
import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;
import persistence.entity.impl.event.EntityEventListener;
import persistence.entity.impl.event.action.UpdateAction;
import persistence.entity.impl.store.EntityPersister;

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
        final ContextSource contextSource = entityEvent.getContextSource();

        final EntityEntry entityEntry = contextSource.getEntityEntry(entity);
        if (entityEntry.isReadOnly()) {
            throw new RuntimeException("해당 Entity는 변경될 수 없습니다.");
        }

        syncEventSource(eventSource, entity);
        syncContextSource(contextSource, entity);
        return clazz.cast(entity);
    }

    @Override
    public void syncContextSource(ContextSource contextSource, Object entity) {
        contextSource.putEntity(entity);
    }

    @Override
    public void syncEventSource(EventSource eventSource, Object entity) {
        eventSource.addAction(new UpdateAction(() -> entityPersister.update(entity)));
    }
}
