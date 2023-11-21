package persistence.action;

import persistence.entity.persister.EntityPersister;

public class EntityInsertAction extends AbstractEntityAction {

    public EntityInsertAction(final EntityPersister entityPersister, final Object entity) {
        super(entityPersister, entity);
    }

    @Override
    public void execute() {
        entityPersister.insert(entity);
    }
}
