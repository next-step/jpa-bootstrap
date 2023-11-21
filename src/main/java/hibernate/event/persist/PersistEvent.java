package hibernate.event.persist;

import hibernate.action.ActionQueue;
import hibernate.action.InsertUpdateActionQueue;
import hibernate.entity.meta.column.EntityColumn;
import hibernate.metamodel.MetaModel;
import jakarta.persistence.GenerationType;

public class PersistEvent<T> {

    private final InsertUpdateActionQueue actionQueue;
    private final T entity;
    private final Class<T> clazz;
    private final EntityColumn entityId;

    private PersistEvent(final ActionQueue actionQueue, final T entity, final Class<T> clazz, final EntityColumn entityId) {
        this.actionQueue = actionQueue;
        this.entity = entity;
        this.clazz = clazz;
        this.entityId = entityId;
    }

    public static <T> PersistEvent<T> createEvent(final ActionQueue actionQueue, final MetaModel metaModel, final T entity) {
        Class<T> clazz = (Class<T>) entity.getClass();
        return new PersistEvent<>(
                actionQueue,
                entity,
                clazz,
                metaModel.getEntityId(clazz)
        );
    }

    public boolean isIdentity() {
        return entityId.getGenerationType() == GenerationType.IDENTITY;
    }

    public InsertUpdateActionQueue getActionQueue() {
        return actionQueue;
    }

    public T getEntity() {
        return entity;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public EntityColumn getEntityId() {
        return entityId;
    }
}
