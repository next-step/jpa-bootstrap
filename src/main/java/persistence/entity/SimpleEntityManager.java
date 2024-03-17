package persistence.entity;

import java.util.List;
import java.util.Objects;
import jdbc.JdbcTemplate;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persistencecontext.EntitySnapshot;
import persistence.entity.persistencecontext.SimplePersistenceContext;
import persistence.entity.persister.EntityPersister;
import persistence.entity.proxy.LazyLoadingContext;
import persistence.entity.proxy.LazyLoadingProxyFactory;
import persistence.sql.meta.Column;
import persistence.sql.meta.MetaModel;
import persistence.sql.meta.SimpleMetaModel;
import persistence.sql.meta.Table;

public class SimpleEntityManager implements EntityManager {

    private final SimplePersistenceContext persistenceContext;
    private final MetaModel metaModel;


    private SimpleEntityManager(JdbcTemplate jdbcTemplate, String basePackage) {
        metaModel = SimpleMetaModel.of(jdbcTemplate, basePackage);
        persistenceContext = new SimplePersistenceContext();
    }

    public static SimpleEntityManager of(JdbcTemplate jdbcTemplate, String basePackage) {
        return new SimpleEntityManager(jdbcTemplate, basePackage);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T find(Class<T> clazz, Long id) {
        T entity = (T) persistenceContext.getEntity(clazz, id);
        if (entity == null) {
            entity = metaModel.getEntityLoader(clazz).find(id);
            cacheEntityWithAssociations(entity, EntityEntry.loading());
            setLazyRelationProxy(entity);
        }
        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T persist(T entity) {
        EntityPersister<T> persister = metaModel.getEntityPersister((Class<T>) entity.getClass());
        persister.insert(entity);
        cacheEntityWithAssociations(entity, EntityEntry.saving());
        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void remove(T entity) {
        EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        entityEntry.deleted();
        persistenceContext.removeEntity(entity);
        EntityPersister<T> persister = metaModel.getEntityPersister((Class<T>) entity.getClass());
        persister.delete(entity);
        entityEntry.gone();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T merge(T entity) {
        EntitySnapshot before = persistenceContext.getCachedDatabaseSnapshot(entity);
        EntitySnapshot after = EntitySnapshot.from(entity);

        if (!Objects.equals(before, after)) {
            EntityPersister<T> persister = metaModel.getEntityPersister((Class<T>) entity.getClass());
            persister.update(entity);
            cacheEntity(entity, EntityEntry.saving());
        }
        return entity;
    }

    private <T> void setLazyRelationProxy(T entity) {
        Table table = Table.getInstance(entity.getClass());
        List<Column> lazyRelationColumns = table.getLazyRelationColumns();

        for (Column lazyRelationColumn : lazyRelationColumns) {
            Table relationTable = lazyRelationColumn.getRelationTable();
            EntityLoader<?> entityLoader = metaModel.getEntityLoader(relationTable.getClazz());
            LazyLoadingContext context = new LazyLoadingContext(table, relationTable, entity,
                entityLoader, this::prepareCacheEntity);
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
