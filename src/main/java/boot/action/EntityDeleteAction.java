package boot.action;

import persistence.entity.EntityPersister;

public class EntityDeleteAction<T> implements EntityAction {

    private final T entity;
    private final EntityPersister<T> entityPersister;

    public EntityDeleteAction(T entity, EntityPersister<T> entityPersister) {
        this.entity = entity;
        this.entityPersister = entityPersister;
    }

    @Override
    public void execute() {
        entityPersister.delete(entity);
    }
}
