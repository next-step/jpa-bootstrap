package persistence.entity.event.action;

import persistence.entity.EntityPersister;

public class EntityInsertAction<T> implements EntityAction {

    private final T entity;
    private final EntityPersister entityPersister;

    public EntityInsertAction(T entity, EntityPersister entityPersister) {
        this.entity = entity;
        this.entityPersister = entityPersister;
    }

    @Override
    public void execute() {
        entityPersister.insert(entity);
    }
}
