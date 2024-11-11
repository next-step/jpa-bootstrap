package persistence.bootstrap;

import jdbc.JdbcTemplate;
import persistence.bootstrap.binder.CollectionLoaderBinder;
import persistence.bootstrap.binder.CollectionPersisterBinder;
import persistence.bootstrap.binder.EntityLoaderBinder;
import persistence.bootstrap.binder.EntityPersisterBinder;
import persistence.bootstrap.binder.EntityTableBinder;
import persistence.bootstrap.binder.RowMapperBinder;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.CollectionPersister;
import persistence.entity.persister.EntityPersister;
import persistence.entity.proxy.ProxyFactory;
import persistence.meta.EntityTable;
import persistence.sql.dml.DmlQueries;

import java.util.List;

public class Metamodel {
    private final EntityTableBinder entityTableBinder;
    private final EntityLoaderBinder entityLoaderBinder;
    private final EntityPersisterBinder entityPersisterBinder;
    private final CollectionPersisterBinder collectionPersisterBinder;

    public Metamodel(JdbcTemplate jdbcTemplate, DmlQueries dmlQueries, ProxyFactory proxyFactory, String... basePackages) {
        final List<Class<?>> entityTypes = new EntityHolder(basePackages).getEntityTypes();

        this.entityTableBinder = new EntityTableBinder(entityTypes);

        RowMapperBinder rowMapperBinder = new RowMapperBinder(entityTypes, entityTableBinder);
        CollectionLoaderBinder collectionLoaderBinder = new CollectionLoaderBinder(entityTypes, entityTableBinder, rowMapperBinder, jdbcTemplate, dmlQueries);

        this.collectionPersisterBinder =
                new CollectionPersisterBinder(entityTypes, entityTableBinder, jdbcTemplate, dmlQueries);
        this.entityLoaderBinder =
                new EntityLoaderBinder(entityTypes, entityTableBinder, collectionLoaderBinder, rowMapperBinder,
                        jdbcTemplate, dmlQueries, proxyFactory);
        this.entityPersisterBinder =
                new EntityPersisterBinder(entityTypes, entityTableBinder, collectionLoaderBinder,
                        jdbcTemplate, dmlQueries);
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
}
