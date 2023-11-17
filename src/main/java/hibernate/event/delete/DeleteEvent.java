package hibernate.event.delete;

import hibernate.entity.EntityPersister;
import hibernate.metamodel.MetaModel;

public class DeleteEvent<T> {

    private final EntityPersister<T> entityPersister;
    private final T entity;

    private DeleteEvent(final EntityPersister<T> entityPersister, final T entity) {
        this.entityPersister = entityPersister;
        this.entity = entity;
    }

    public static <T> DeleteEvent<T> createEvent(final MetaModel metaModel, final T entity) {
        return new DeleteEvent<T>(metaModel.getEntityPersister((Class<T>) entity.getClass()), entity);
    }

    public EntityPersister<T> getEntityPersister() {
        return entityPersister;
    }

    public T getEntity() {
        return entity;
    }
}
