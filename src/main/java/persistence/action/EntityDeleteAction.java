package persistence.action;

import persistence.entity.persister.EntityPersister;

public class EntityDeleteAction extends AbstractEntityAction {
    public EntityDeleteAction(final EntityPersister entityPersister, final Object entity) {
        super(entityPersister, entity);
    }

    @Override
    public void execute() {
        entityPersister.delete(entity);
    }
}
