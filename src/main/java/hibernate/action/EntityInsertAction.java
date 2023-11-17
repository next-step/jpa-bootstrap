package hibernate.action;

import hibernate.entity.EntityPersister;

public class EntityInsertAction<T> extends AbstractEntityAction<T> {

    private final T entity;

    public EntityInsertAction(final EntityPersister<T> entityPersister, final T entity) {
        super(entityPersister);
        this.entity = entity;
    }

    @Override
    public void execute() {
        entityPersister.insert(entity);
    }
}
