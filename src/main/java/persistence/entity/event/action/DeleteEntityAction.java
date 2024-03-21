package persistence.entity.event.action;

import persistence.entity.persister.EntityPersister;

public class DeleteEntityAction<T> extends AbstractEntityAction<T> {
    public DeleteEntityAction(EntityPersister<T> persister, T entity) {
        super(persister, entity);
    }

    @Override
    public void execute() {
        persister.delete(entity);
    }
}
