package hibernate.event.persist;

import hibernate.entity.meta.column.EntityColumn;
import hibernate.metamodel.MetaModel;
import jakarta.persistence.GenerationType;

public class PersistEvent<T> {

    private final T entity;
    private final Class<T> clazz;
    private final EntityColumn entityId;

    private PersistEvent(final T entity, final Class<T> clazz, final EntityColumn entityId) {
        this.entity = entity;
        this.clazz = clazz;
        this.entityId = entityId;
    }

    public static <T> PersistEvent<T> createEvent(final MetaModel metaModel, final T entity) {
        Class<T> clazz = (Class<T>) entity.getClass();
        return new PersistEvent<>(
                entity,
                clazz,
                metaModel.getEntityId(clazz)
        );
    }

    public boolean isIdentity() {
        return entityId.getGenerationType() == GenerationType.IDENTITY;
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
