package hibernate.action;

import hibernate.entity.EntityPersister;

import java.util.Objects;

public class EntityBasicInsertAction<T> extends EntityInsertAction<T> {

    private final Object entityId;

    public EntityBasicInsertAction(final EntityPersister<T> entityPersister, final T entity, final Object entityId) {
        super(entityPersister, entity);
        this.entityId = entityId;
    }

    @Override
    public void execute() {
        entityPersister.insert(entity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityBasicInsertAction<?> that = (EntityBasicInsertAction<?>) o;
        return Objects.equals(entityId, that.entityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId);
    }
}
