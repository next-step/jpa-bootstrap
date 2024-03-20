package boot.action;

import persistence.entity.EntityPersister;

public class EntityUpdateAction<T> implements EntityAction {

    private final EntityPersister<T> entityPersister;
    private final T entity;

    public EntityUpdateAction(EntityPersister<T> entityPersister, T entity) {
        this.entityPersister = entityPersister;
        this.entity = entity;
    }

    @Override
    public void execute() {
        entityPersister.update(entity);

    }
}
