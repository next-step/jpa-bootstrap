package hibernate.event.delete;

import hibernate.action.ActionQueue;
import hibernate.action.DeleteActionQueue;
import hibernate.entity.EntityPersister;
import hibernate.metamodel.MetaModel;

public class DeleteEvent<T> {

    private final DeleteActionQueue actionQueue;
    private final EntityPersister<T> entityPersister;
    private final T entity;

    private DeleteEvent(final DeleteActionQueue actionQueue, final EntityPersister<T> entityPersister, final T entity) {
        this.actionQueue = actionQueue;
        this.entityPersister = entityPersister;
        this.entity = entity;
    }

    public static <T> DeleteEvent<T> createEvent(final ActionQueue actionQueue, final MetaModel metaModel, final T entity) {
        return new DeleteEvent<>(
                actionQueue,
                metaModel.getEntityPersister((Class<T>) entity.getClass()),
                entity);
    }

    public DeleteActionQueue getActionQueue() {
        return actionQueue;
    }

    public EntityPersister<T> getEntityPersister() {
        return entityPersister;
    }

    public T getEntity() {
        return entity;
    }
}
