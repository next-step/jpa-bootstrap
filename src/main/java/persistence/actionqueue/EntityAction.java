package persistence.actionqueue;

import persistence.entity.persister.EntityPersister;

import java.util.Objects;

public abstract class EntityAction {
    protected final Object instance;
    protected final Object snapshot;
    protected final EntityPersister persister;

    public EntityAction(Object instance, EntityPersister persister, Object snapshot) {
        this.instance = instance;
        this.persister = persister;
        this.snapshot = snapshot;
    }

    public abstract void execute();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityAction)) return false;
        EntityAction that = (EntityAction) o;
        return Objects.equals(instance, that.instance) && Objects.equals(snapshot, that.snapshot) && Objects.equals(persister, that.persister);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance, snapshot, persister);
    }
}
