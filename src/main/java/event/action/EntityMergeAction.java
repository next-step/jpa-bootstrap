package event.action;

import builder.dml.EntityData;
import persistence.EntityPersister;

public class EntityMergeAction implements EntityAction{

    private final EntityData entityData;
    private final EntityPersister entityPersister;

    public EntityMergeAction(EntityData entityData, EntityPersister entityPersister) {
        this.entityData = entityData;
        this.entityPersister = entityPersister;
    }

    @Override
    public void execute() {
        this.entityPersister.merge(entityData);
    }
}
