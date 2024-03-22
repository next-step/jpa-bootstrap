package persistence.entity.event.action;

import persistence.entity.persister.EntityPersister;

public class InsertEntityAction<T> extends AbstractEntityAction<T> {
    public InsertEntityAction(EntityPersister<T> persister, T entity) {
        super(persister, entity);
    }

    @Override
    public void execute() {
        persister.insert(entity);
    }
}
