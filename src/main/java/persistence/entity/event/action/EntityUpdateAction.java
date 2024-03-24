package persistence.entity.event.action;

import persistence.entity.EntityPersister;

import java.util.Objects;

public class EntityUpdateAction<T, ID> implements EntityAction{

    private final T entity;
    private final ID id;
    private final EntityPersister entityPersister;

    public EntityUpdateAction(T entity, ID id, EntityPersister entityPersister) {
        this.entity = entity;
        this.id = id;
        this.entityPersister = entityPersister;
    }

    @Override
    public void execute() {
        entityPersister.update(entity, id);
    }

    @Override
    public String toString() {
        return "EntityUpdateAction{" +
                "entity=" + entity +
                ", id=" + id +
                ", entityPersister=" + entityPersister +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityUpdateAction<?, ?> that = (EntityUpdateAction<?, ?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
