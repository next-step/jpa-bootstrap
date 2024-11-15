package persistence.action;

import persistence.bootstrap.Metamodel;
import persistence.entity.manager.EntityStatus;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.entity.persister.CollectionPersister;
import persistence.entity.persister.EntityPersister;
import persistence.meta.EntityTable;

public class PersistAction<T> {
    private final Metamodel metamodel;
    private final PersistenceContext persistenceContext;
    private final T entity;

    public PersistAction(Metamodel metamodel, PersistenceContext persistenceContext, T entity) {
        this.metamodel = metamodel;
        this.persistenceContext = persistenceContext;
        this.entity = entity;
    }

    public void execute() {
        final EntityPersister entityPersister = metamodel.getEntityPersister(entity.getClass());
        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());

        entityPersister.insert(entity);
        if (entityTable.isOneToMany()) {
            final CollectionPersister collectionPersister =
                    metamodel.getCollectionPersister(entity.getClass(), entityTable.getAssociationColumnName());
            collectionPersister.insert(entityTable.getAssociationColumnValue(entity), entity);
        }
        persistenceContext.createOrUpdateStatus(entity, EntityStatus.MANAGED);
    }
}
