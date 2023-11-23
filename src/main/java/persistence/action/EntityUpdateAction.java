package persistence.action;

import persistence.entity.persister.EntityPersister;

public class EntityUpdateAction extends AbstractEntityAction {

    public EntityUpdateAction(final EntityPersister entityPersister, final Object entity) {
        super(entityPersister, entity);
    }

    @Override
    public void execute() {
        entityPersister.update(entity);
    }
}
