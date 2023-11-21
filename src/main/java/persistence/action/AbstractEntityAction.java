package persistence.action;

import persistence.entity.persister.EntityPersister;

import java.util.Objects;

public abstract class AbstractEntityAction {
    protected final EntityPersister entityPersister;

    protected final Object entity;

    public AbstractEntityAction(final EntityPersister entityPersister, final Object entity) {
        this.entityPersister = entityPersister;
        this.entity = entity;
    }

    public abstract void execute();

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final AbstractEntityAction that = (AbstractEntityAction) object;
        return Objects.equals(entityPersister, that.entityPersister) && Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityPersister, entity);
    }
}
