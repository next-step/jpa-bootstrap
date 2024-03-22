package persistence.entity.event.action;

import persistence.entity.EntityPersister;

import java.util.Objects;

public class EntityDeleteAction<T, ID> implements EntityAction {
    private final T entity;
    private final ID id;
    private final EntityPersister entityPersister;

    public EntityDeleteAction(T entity, ID id, EntityPersister entityPersister) {
        this.entity = entity;
        this.id = id;
        this.entityPersister = entityPersister;
    }

    @Override
    public void execute() {
        entityPersister.delete(entity, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityDeleteAction<?, ?> that = (EntityDeleteAction<?, ?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
