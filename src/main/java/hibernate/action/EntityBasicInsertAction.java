package hibernate.action;

import hibernate.entity.EntityPersister;

public class EntityBasicInsertAction<T> extends EntityInsertAction<T> {

    public EntityBasicInsertAction(final EntityPersister<T> entityPersister, final T entity) {
        super(entityPersister, entity);
    }

    @Override
    public void execute() {
        entityPersister.insert(entity);
    }
}
