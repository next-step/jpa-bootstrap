package hibernate.event.persist;

import hibernate.action.ActionQueue;
import hibernate.entity.EntityPersister;
import hibernate.entity.meta.column.EntityColumn;
import hibernate.event.AbstractEntityEvent;
import hibernate.metamodel.MetaModel;
import jakarta.persistence.GenerationType;

public class PersistEvent<T> extends AbstractEntityEvent {

    private final EntityPersister<T> entityPersister;
    private final T entity;
    private final EntityColumn entityId;

    private PersistEvent(final ActionQueue actionQueue, final EntityPersister<T> entityPersister, final T entity, final EntityColumn entityId) {
        super(actionQueue);
        this.entityPersister = entityPersister;
        this.entity = entity;
        this.entityId = entityId;
    }

    public static <T> PersistEvent<T> createEvent(final ActionQueue actionQueue, final MetaModel metaModel, final T entity) {
        return new PersistEvent<>(
                actionQueue,
                metaModel.getEntityPersister((Class<T>) entity.getClass()),
                entity,
                metaModel.getEntityId(entity.getClass())
        );
    }

    public boolean isIdentity() {
        return entityId.getGenerationType() == GenerationType.IDENTITY;
    }

    public EntityPersister<T> getEntityPersister() {
        return entityPersister;
    }

    public T getEntity() {
        return entity;
    }

    public EntityColumn getEntityId() {
        return entityId;
    }
}
