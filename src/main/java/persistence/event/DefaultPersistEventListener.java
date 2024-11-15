package persistence.event;

import persistence.bootstrap.Metamodel;
import persistence.entity.manager.EntityStatus;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.entity.persister.CollectionPersister;
import persistence.entity.persister.EntityPersister;
import persistence.meta.EntityTable;

public class DefaultPersistEventListener implements PersistEventListener {
    @Override
    public <T> void onPersist(PersistEvent<T> persistEvent) {
        final Metamodel metamodel = persistEvent.getMetamodel();
        final PersistenceContext persistenceContext = persistEvent.getPersistenceContext();
        final T entity = persistEvent.getEntity();

        final EntityPersister entityPersister = metamodel.getEntityPersister(entity.getClass());
        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());

        entityPersister.insert(entity);
        if (entityTable.isOneToMany()) {
            final CollectionPersister collectionPersister =
                    metamodel.getCollectionPersister(entity.getClass(), entityTable.getAssociationColumnName());
            collectionPersister.insert(entityTable.getAssociationColumnValue(entity), entity);
        }

        persistenceContext.addEntity(entity, entityTable.getIdValue(entity));
        persistenceContext.createOrUpdateStatus(entity, EntityStatus.MANAGED);
    }
}
