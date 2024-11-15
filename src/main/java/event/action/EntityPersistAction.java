package event.action;

import builder.dml.EntityData;
import persistence.EntityPersister;

public class EntityPersistAction implements EntityAction{

    private final EntityData entityData;
    private final EntityPersister entityPersister;

    public EntityPersistAction(EntityData entityData, EntityPersister entityPersister) {
        this.entityData = entityData;
        this.entityPersister = entityPersister;
    }

    @Override
    public void execute() {
        this.entityPersister.persist(entityData);
    }
}
