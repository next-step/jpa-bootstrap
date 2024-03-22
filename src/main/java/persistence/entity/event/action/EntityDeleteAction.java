package persistence.entity.event.action;

import persistence.entity.EntityPersister;

public class EntityDeleteAction<T, ID> implements EntityAction {
    private final T entity;
    private final ID id;
    private final EntityPersister entityPersister;

    public EntityDeleteAction(T entity, ID id, EntityPersister entityPersister) {
        this.entity = entity;
        this.id = id;
        this.entityPersister = entityPersister;
    }

    @Override
    public void execute() {
        entityPersister.delete(entity, id);
    }

}
