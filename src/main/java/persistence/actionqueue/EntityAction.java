package persistence.actionqueue;

import persistence.entity.persister.EntityPersister;

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
}
