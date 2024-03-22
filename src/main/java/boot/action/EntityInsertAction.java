package boot.action;

import java.util.Objects;
import persistence.entity.EntityPersister;

public class EntityInsertAction<T> implements EntityAction {

    private final T entity;
    private final EntityPersister<T> entityPersister;

    public EntityInsertAction(T entity, EntityPersister<T> entityPersister) {
        this.entity = entity;
        this.entityPersister = entityPersister;
    }

    @Override
    public void execute() {
        entityPersister.insert(entity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityInsertAction<?> that = (EntityInsertAction<?>) o;
        return Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity);
    }
}
