package hibernate.action;

import hibernate.entity.EntityPersister;

public abstract class EntityInsertAction<T> extends AbstractEntityAction<T> {

    protected final T entity;

    public EntityInsertAction(EntityPersister<T> entityPersister, T entity) {
        super(entityPersister);
        this.entity = entity;
    }
}
