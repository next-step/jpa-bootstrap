package persistence.entity.event.action;

import persistence.entity.EntityPersister;

public class EntityUpdateAction<T, ID> implements EntityAction{

    private final T entity;
    private final ID id;
    private final EntityPersister entityPersister;

    public EntityUpdateAction(T entity, ID id, EntityPersister entityPersister) {
        this.entity = entity;
        this.id = id;
        this.entityPersister = entityPersister;
    }

    @Override
    public void execute() {
        entityPersister.update(entity, id);
    }
}
