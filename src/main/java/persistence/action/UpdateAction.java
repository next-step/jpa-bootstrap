package persistence.action;

import persistence.bootstrap.Metamodel;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.entity.persister.EntityPersister;
import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;

import java.util.List;

public class UpdateAction<T> {
    private final Metamodel metamodel;
    private final PersistenceContext persistenceContext;
    private final T entity;
    private final List<EntityColumn> dirtiedEntityColumns;

    public UpdateAction(Metamodel metamodel, PersistenceContext persistenceContext, T entity, List<EntityColumn> dirtiedEntityColumns) {
        this.metamodel = metamodel;
        this.persistenceContext = persistenceContext;
        this.entity = entity;
        this.dirtiedEntityColumns = dirtiedEntityColumns;
    }

    public void execute() {
        final EntityPersister entityPersister = metamodel.getEntityPersister(entity.getClass());
        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());

        entityPersister.update(entity, dirtiedEntityColumns);
        persistenceContext.addEntity(entity, entityTable);
    }
}
