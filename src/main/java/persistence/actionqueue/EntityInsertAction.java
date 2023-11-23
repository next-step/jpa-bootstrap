package persistence.actionqueue;

import persistence.entity.persister.EntityPersister;

public class EntityInsertAction extends EntityAction {
    public EntityInsertAction(Object instance, EntityPersister persister, Object snapshot) {
        super(instance, persister, snapshot);
    }

    @Override
    public void execute() {
        super.persister.insert(super.instance);
    }
}
