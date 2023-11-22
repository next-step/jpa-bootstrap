package hibernate.action;

import hibernate.entity.EntityPersister;

public abstract class AbstractEntityAction<T> implements EntityAction {

    protected final EntityPersister<T> entityPersister;

    public AbstractEntityAction(EntityPersister<T> entityPersister) {
        this.entityPersister = entityPersister;
    }
}
