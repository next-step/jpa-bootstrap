package persistence.entity.impl.event.listener;

import persistence.entity.EntityEntry;
import persistence.entity.ContextSource;
import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;
import persistence.entity.impl.event.EntityEventListener;
import persistence.entity.impl.event.action.DeleteAction;
import persistence.entity.impl.event.action.UpdateAction;
import persistence.entity.impl.store.EntityPersister;

public class DeleteEntityEventListenerImpl implements EntityEventListener {

    private final EntityPersister entityPersister;

    public DeleteEntityEventListenerImpl(EntityPersister entityPersister) {
        this.entityPersister = entityPersister;
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        final Object entity = entityEvent.getEntity();
        final ContextSource contextSource = entityEvent.getContextSource();
        final EventSource eventSource = entityEvent.getEventSource();

        final EntityEntry entityEntry = contextSource.getEntityEntry(entity);
        if (entityEntry.isReadOnly()) {
            throw new RuntimeException("해당 Entity는 삭제될 수 없습니다.");
        }

        contextSource.deleted(entity);

        syncEventSource(eventSource, entity);
        syncContextSource(contextSource, entity);
    }

    @Override
    public <T> T onEvent(Class<T> clazz, EntityEvent entityEvent) {
        throw new RuntimeException("DeleteEventListener는 반환이 있는 이벤트를 지원하지 않습니다.");
    }

    @Override
    public void syncContextSource(ContextSource contextSource, Object entity) {
        contextSource.purgeEntity(entity);
    }

    @Override
    public void syncEventSource(EventSource eventSource, Object entity) {
        eventSource.addAction(new DeleteAction(() -> entityPersister.delete(entity)));
    }
}
