package persistence.entity.impl.event.listener;

import persistence.entity.ContextSource;
import persistence.entity.EventSource;
import persistence.entity.impl.event.EntityEvent;
import persistence.entity.impl.event.EntityEventListener;
import persistence.entity.impl.event.action.InsertAction;
import persistence.entity.impl.store.EntityPersister;

public class PersistEntityEventListenerImpl implements EntityEventListener {

    private final EntityPersister entityPersister;

    public PersistEntityEventListenerImpl(EntityPersister entityPersister) {
        this.entityPersister = entityPersister;
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        final Object identityEntity = entityEvent.getEntity();

        final ContextSource contextSource = entityEvent.getContextSource();
        final EventSource eventSource = entityEvent.getEventSource();

        contextSource.saving(identityEntity);

        syncEventSource(eventSource, identityEntity);
        syncContextSource(contextSource, identityEntity);
    }

    @Override
    public <T> T onEvent(Class<T> clazz, EntityEvent entityEvent) {
        final Object savedEntity = entityPersister.store(entityEvent.getEntity());
        final ContextSource contextSource = entityEvent.getContextSource();
        final EventSource eventSource = entityEvent.getEventSource();

        contextSource.saving(savedEntity);

        syncEventSource(eventSource, entityEvent.getEntity());
        syncContextSource(contextSource, savedEntity);
        return clazz.cast(savedEntity);
    }

    @Override
    public void syncContextSource(ContextSource contextSource, Object entity) {
        contextSource.putEntity(entity);
    }

    @Override
    public void syncEventSource(EventSource eventSource, Object entity) {
        eventSource.addAction(new InsertAction(() -> entityPersister.store(entity)));
    }
}
