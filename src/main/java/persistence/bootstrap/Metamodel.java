package persistence.bootstrap;

import jdbc.JdbcTemplate;
import persistence.bootstrap.binder.CollectionLoaderBinder;
import persistence.bootstrap.binder.CollectionPersisterBinder;
import persistence.bootstrap.binder.EntityAssociationBinder;
import persistence.bootstrap.binder.EntityBinder;
import persistence.bootstrap.binder.EntityLoaderBinder;
import persistence.bootstrap.binder.EntityPersisterBinder;
import persistence.bootstrap.binder.EntityTableBinder;
import persistence.bootstrap.binder.RowMapperBinder;
import persistence.dialect.Dialect;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.CollectionPersister;
import persistence.entity.persister.EntityPersister;
import persistence.entity.proxy.ProxyFactory;
import persistence.meta.EntityTable;

import java.util.List;

public class Metamodel {
    private final JdbcTemplate jdbcTemplate;
    private final EntityTableBinder entityTableBinder;
    private final EntityAssociationBinder entityAssociationBinder;
    private final EntityLoaderBinder entityLoaderBinder;
    private final EntityPersisterBinder entityPersisterBinder;
    private final CollectionPersisterBinder collectionPersisterBinder;

    public Metamodel(JdbcTemplate jdbcTemplate, Dialect dialect, ProxyFactory proxyFactory, String... basePackages) {
        this.jdbcTemplate = jdbcTemplate;

        final List<Class<?>> entityTypes = new EntityBinder(basePackages).getEntityTypes();

        this.entityTableBinder = new EntityTableBinder(entityTypes);
        this.entityAssociationBinder = new EntityAssociationBinder(entityTableBinder);

        RowMapperBinder rowMapperBinder = new RowMapperBinder(entityTypes, entityTableBinder);
        CollectionLoaderBinder collectionLoaderBinder =
                new CollectionLoaderBinder(entityTypes, entityTableBinder, rowMapperBinder, jdbcTemplate);

        this.collectionPersisterBinder =
                new CollectionPersisterBinder(entityTypes, entityTableBinder, jdbcTemplate);
        this.entityLoaderBinder =
                new EntityLoaderBinder(entityTypes, entityTableBinder, collectionLoaderBinder, rowMapperBinder,
                        jdbcTemplate, proxyFactory);
        this.entityPersisterBinder =
                new EntityPersisterBinder(entityTypes, entityTableBinder, jdbcTemplate);

        DatabaseSyncManager.sync(entityTableBinder, entityAssociationBinder, jdbcTemplate, dialect);
    }

    public EntityTable getEntityTable(Class<?> entityType) {
        return entityTableBinder.getEntityTable(entityType);
    }

    public EntityLoader getEntityLoader(Class<?> entityType) {
        return entityLoaderBinder.getEntityLoader(entityType);
    }

    public EntityPersister getEntityPersister(Class<?> entityType) {
        return entityPersisterBinder.getEntityPersister(entityType);
    }

    public CollectionPersister getCollectionPersister(Class<?> entityType, String columnName) {
        return collectionPersisterBinder.getCollectionPersister(entityType, columnName);
    }

    public void close() {
        DatabaseSyncManager.clear(entityTableBinder, jdbcTemplate);

        entityTableBinder.clear();
        entityAssociationBinder.clear();
        entityLoaderBinder.clear();
        entityPersisterBinder.clear();
        collectionPersisterBinder.clear();
    }
}
