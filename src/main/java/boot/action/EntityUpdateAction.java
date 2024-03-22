package boot.action;

import java.util.Objects;
import persistence.entity.EntityPersister;

public class EntityUpdateAction<T> implements EntityAction {

    private final EntityPersister<T> entityPersister;
    private final T entity;

    public EntityUpdateAction(EntityPersister<T> entityPersister, T entity) {
        this.entityPersister = entityPersister;
        this.entity = entity;
    }

    @Override
    public void execute() {
        entityPersister.update(entity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityUpdateAction<?> that = (EntityUpdateAction<?>) o;
        return Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity);
    }
}
