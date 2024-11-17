package persistence.action;

import persistence.bootstrap.Metamodel;
import persistence.entity.persister.EntityPersister;

public class DeleteAction<T> {
    private final Metamodel metamodel;
    private final T entity;

    public DeleteAction(Metamodel metamodel, T entity) {
        this.metamodel = metamodel;
        this.entity = entity;
    }

    public void execute() {
        final EntityPersister entityPersister = metamodel.getEntityPersister(entity.getClass());
        entityPersister.delete(entity);
    }
}
