package persistence.actionqueue;

import persistence.entity.persister.EntityPersister;

public class EntityUpdateAction extends EntityAction {

    public EntityUpdateAction(Object instance, EntityPersister persister, Object snapshot) {
        super(instance, persister, snapshot);
    }

    @Override
    public void execute() {
        persister.update(snapshot, instance);
    }
}
