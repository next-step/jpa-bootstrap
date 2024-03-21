package persistence.entity.entitymanager;

import java.util.List;
import java.util.Objects;
import persistence.entity.event.EntityEvent;
import persistence.entity.event.EventType;
import persistence.entity.event.listener.EntityEventDispatcher;
import persistence.entity.persistencecontext.EntitySnapshot;
import persistence.entity.persistencecontext.SimplePersistenceContext;
import persistence.entity.proxy.LazyLoadingContext;
import persistence.entity.proxy.LazyLoadingProxyFactory;
import persistence.sql.meta.Column;
import persistence.sql.meta.MetaModel;
import persistence.sql.meta.Table;

public class SimpleEntityManager implements EntityManager {

    private final SimplePersistenceContext persistenceContext;
    private final EntityEventDispatcher entityEventDispatcher;

    private SimpleEntityManager(MetaModel metaModel) {
        persistenceContext = new SimplePersistenceContext();
        entityEventDispatcher = new EntityEventDispatcher(metaModel);
    }

    public static SimpleEntityManager from(MetaModel metaModel) {
        return new SimpleEntityManager(metaModel);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T find(Class<T> clazz, Long id) {
        T entity = persistenceContext.getEntity(clazz, id);
        if (entity == null) {
            entity = (T) entityEventDispatcher.dispatch(new EntityEvent<>(clazz, id), EventType.LOAD);
            cacheEntityWithAssociations(entity, EntityEntry.loading());
            setLazyRelationProxy(entity);
        }
        return entity;
    }

    @Override
    public <T> T persist(T entity) {
        entityEventDispatcher.dispatch(new EntityEvent<>(entity), EventType.PERSIST);
        cacheEntityWithAssociations(entity, EntityEntry.saving());
        return entity;
    }

    @Override
    public <T> void remove(T entity) {
        EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        entityEntry.deleted();
        persistenceContext.removeEntity(entity);
        entityEventDispatcher.dispatch(new EntityEvent<>(entity), EventType.DELETE);
        entityEntry.gone();
    }

    @Override
    public <T> T merge(T entity) {
        EntitySnapshot before = persistenceContext.getCachedDatabaseSnapshot(entity);
        EntitySnapshot after = EntitySnapshot.from(entity);

        if (!Objects.equals(before, after)) {
            entityEventDispatcher.dispatch(new EntityEvent<>(entity), EventType.MERGE);
            cacheEntity(entity, EntityEntry.saving());
        }
        return entity;
    }

    @Override
    public void close() {
        SessionContext sessionContext = ThreadLocalSessionContext.getInstance();
        sessionContext.close();
    }

    private <T> void setLazyRelationProxy(T entity) {
        Table table = Table.getInstance(entity.getClass());
        List<Column> lazyRelationColumns = table.getLazyRelationColumns();

        for (Column lazyRelationColumn : lazyRelationColumns) {
            Table relationTable = lazyRelationColumn.getRelationTable();
            LazyLoadingContext context = new LazyLoadingContext(table, relationTable, entity,
                entityEventDispatcher, this::prepareCacheEntity);
            lazyRelationColumn.setFieldValue(entity, LazyLoadingProxyFactory.createProxy(context));
        }
    }

    private <T> void cacheEntityWithAssociations(T entity, EntityEntry entityEntry) {
        if (persistenceContext.getCachedDatabaseSnapshot(entity) == null) {
            cacheEntity(entity, entityEntry);
            cacheAssociations(entity);
            entityEntry.managed();
        }
    }

    private <T> void cacheEntity(T t, EntityEntry entityEntry) {
        persistenceContext.addEntity(t);
        persistenceContext.getDatabaseSnapshot(t);
        persistenceContext.setEntityEntry(t, entityEntry);
    }

    private <T> void prepareCacheEntity(T t) {
        if (t instanceof Iterable) {
            ((Iterable<?>) t).forEach(entity -> cacheEntityWithAssociations(entity, EntityEntry.loading()));
            return;
        }
        cacheEntityWithAssociations(t, EntityEntry.loading());
    }

    private <T> void cacheAssociations(T t) {
        Table table = Table.getInstance(t.getClass());
        table.getEagerRelationTables().forEach(relationTable -> {
            Object relationEntity = table.getRelationValue(t, relationTable);
            prepareCacheEntity(relationEntity);
        });
    }
}
