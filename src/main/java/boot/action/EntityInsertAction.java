package boot.action;

import persistence.entity.EntityPersister;

public class EntityInsertAction<T> implements EntityAction {

    private final T entity;
    private final EntityPersister<T> entityPersister;

    public EntityInsertAction(T entity, EntityPersister<T> entityPersister) {
        this.entity = entity;
        this.entityPersister = entityPersister;
    }

    @Override
    public void execute() {
        entityPersister.insert(entity);
    }
}
