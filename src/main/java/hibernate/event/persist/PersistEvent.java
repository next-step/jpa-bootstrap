package hibernate.event.persist;

import hibernate.entity.EntityPersister;
import hibernate.metamodel.MetaModel;

public class PersistEvent<T> {

    private final EntityPersister<T> entityPersister;
    private final T entity;

    private PersistEvent(final EntityPersister<T> entityPersister, final T entity) {
        this.entityPersister = entityPersister;
        this.entity = entity;
    }

    public static <T> PersistEvent<T> createEvent(final MetaModel metaModel, final T entity) {
        return new PersistEvent<>(metaModel.getEntityPersister((Class<T>) entity.getClass()), entity);
    }

    public EntityPersister<T> getEntityPersister() {
        return entityPersister;
    }

    public T getEntity() {
        return entity;
    }
}
