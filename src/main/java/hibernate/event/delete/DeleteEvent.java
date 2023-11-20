package hibernate.event.delete;

import hibernate.action.ActionQueue;
import hibernate.entity.EntityPersister;
import hibernate.event.AbstractEntityEvent;
import hibernate.metamodel.MetaModel;

public class DeleteEvent<T> extends AbstractEntityEvent {

    private final EntityPersister<T> entityPersister;
    private final T entity;

    private DeleteEvent(final ActionQueue actionQueue, final EntityPersister<T> entityPersister, final T entity) {
        super(actionQueue);
        this.entityPersister = entityPersister;
        this.entity = entity;
    }

    public static <T> DeleteEvent<T> createEvent(final ActionQueue actionQueue, final MetaModel metaModel, final T entity) {
        return new DeleteEvent<>(
                actionQueue,
                metaModel.getEntityPersister((Class<T>) entity.getClass()),
                entity);
    }

    public EntityPersister<T> getEntityPersister() {
        return entityPersister;
    }

    public T getEntity() {
        return entity;
    }
}
