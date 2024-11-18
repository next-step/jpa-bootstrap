package persistence.event.persist;

import persistence.action.ActionQueue;
import persistence.action.PersistAction;
import persistence.bootstrap.Metamodel;
import persistence.entity.manager.EntityEntry;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.entity.persister.CollectionPersister;
import persistence.entity.persister.EntityPersister;
import persistence.event.Event;
import persistence.meta.EntityTable;

public class DefaultPersistEventListener implements PersistEventListener {
    public static final String NOT_PERSISTABLE_STATUS_FAILED_MESSAGE = "엔티티가 영속화 가능한 상태가 아닙니다.";

    private final Metamodel metamodel;
    private final PersistenceContext persistenceContext;
    private final ActionQueue actionQueue;

    public DefaultPersistEventListener(Metamodel metamodel, PersistenceContext persistenceContext, ActionQueue actionQueue) {
        this.metamodel = metamodel;
        this.persistenceContext = persistenceContext;
        this.actionQueue = actionQueue;
    }

    @Override
    public <T> void on(Event<T> event) {
        final T entity = event.getEntity();

        final EntityPersister entityPersister = metamodel.getEntityPersister(entity.getClass());
        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());

        validate(entity, persistenceContext);

        if (entityTable.isIdGenerationFromDatabase()) {
            persistImmediately(entityPersister, entity, entityTable, metamodel, persistenceContext);
            return;
        }

        persistLazy(persistenceContext, entity, actionQueue, metamodel);
    }

    private <T> void persistImmediately(EntityPersister entityPersister, T entity, EntityTable entityTable,
                                        Metamodel metamodel, PersistenceContext persistenceContext) {
        entityPersister.insert(entity);
        persistCollection(entityTable, metamodel, entity);

    }

    private <T> void persistLazy(PersistenceContext persistenceContext, T entity, ActionQueue actionQueue, Metamodel metamodel) {
        actionQueue.addAction(new PersistAction<>(metamodel, persistenceContext, entity));
    }

    private void validate(Object entity, PersistenceContext persistenceContext) {
        final EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        if (entityEntry != null && !entityEntry.isPersistable()) {
            throw new IllegalStateException(NOT_PERSISTABLE_STATUS_FAILED_MESSAGE);
        }
    }

    private <T> void persistCollection(EntityTable entityTable, Metamodel metamodel, T entity) {
        if (entityTable.isOneToMany()) {
            final CollectionPersister collectionPersister =
                    metamodel.getCollectionPersister(entity.getClass(), entityTable.getAssociationColumnName());
            collectionPersister.insert(entityTable.getAssociationColumnValue(entity), entity);
        }
    }
}
