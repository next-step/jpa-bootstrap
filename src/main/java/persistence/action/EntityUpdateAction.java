package persistence.action;

import persistence.entity.EntityPersister;

public class EntityUpdateAction {

    private final Object entity;
    private final EntityPersister entityPersister;

    public EntityUpdateAction(Object entity,
                              EntityPersister entityPersister) {

        this.entity = entity;
        this.entityPersister = entityPersister;
    }

    public void execute() {
        entityPersister.update(entity);
    }
}
