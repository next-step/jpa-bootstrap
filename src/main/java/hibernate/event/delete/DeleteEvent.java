package hibernate.event.delete;

import hibernate.action.ActionQueue;
import hibernate.entity.EntityPersister;
import hibernate.entity.EntitySource;
import hibernate.event.AbstractEntityEvent;

public class DeleteEvent<T> extends AbstractEntityEvent {

    private final EntityPersister<T> entityPersister;
    private final T entity;

    private DeleteEvent(final ActionQueue actionQueue, final EntityPersister<T> entityPersister, final T entity) {
        super(actionQueue);
        this.entityPersister = entityPersister;
        this.entity = entity;
    }

    public static <T> DeleteEvent<T> createEvent(final EntitySource entitySource, final T entity) {
        return new DeleteEvent<T>(
                entitySource.getActionQueue(),
                entitySource.getMetaModel().getEntityPersister((Class<T>) entity.getClass()),
                entity
        );
    }

    public EntityPersister<T> getEntityPersister() {
        return entityPersister;
    }

    public T getEntity() {
        return entity;
    }
}
