package persistence.entity.event.action;

import persistence.entity.persister.EntityPersister;

public class UpdateEntityAction<T> extends AbstractEntityAction<T> {
    public UpdateEntityAction(EntityPersister<T> persister, T entity) {
        super(persister, entity);
    }

    @Override
    public void execute() {
        persister.update(entity);
    }
}
