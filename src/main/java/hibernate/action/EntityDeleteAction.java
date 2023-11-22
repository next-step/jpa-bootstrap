package hibernate.action;

import hibernate.entity.EntityPersister;

public class EntityDeleteAction<T> extends AbstractEntityAction<T> {

    private final T entity;

    public EntityDeleteAction(final EntityPersister<T> entityPersister, final T entity) {
        super(entityPersister);
        this.entity = entity;
    }

    @Override
    public void execute() {
        entityPersister.delete(entity);
    }
}
