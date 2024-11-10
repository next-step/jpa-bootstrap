package persistence.bootstrap;

import jdbc.JdbcTemplate;
import persistence.entity.CollectionLoader;
import persistence.entity.CollectionPersister;
import persistence.entity.EntityLoader;
import persistence.entity.EntityPersister;
import persistence.entity.proxy.ProxyFactory;
import persistence.meta.EntityTable;
import persistence.sql.dml.DmlQueries;

public class Metamodel {
    private final EntityTableBinder entityTableBinder;
    private final EntityLoaderBinder entityLoaderBinder;
    private final EntityPersisterBinder entityPersisterBinder;
    private final CollectionLoaderBinder collectionLoaderBinder;
    private final CollectionPersisterBinder collectionPersisterBinder;

    public Metamodel(JdbcTemplate jdbcTemplate, DmlQueries dmlQueries, ProxyFactory proxyFactory, String... basePackages) {
        final EntityHolder entityHolder = new EntityHolder(basePackages);
        this.entityTableBinder = new EntityTableBinder(entityHolder.getEntityTypes());
        this.collectionLoaderBinder =
                new CollectionLoaderBinder(entityHolder.getEntityTypes(), entityTableBinder, jdbcTemplate, dmlQueries);
        this.collectionPersisterBinder =
                new CollectionPersisterBinder(entityHolder.getEntityTypes(), entityTableBinder, jdbcTemplate, dmlQueries);
        this.entityLoaderBinder =
                new EntityLoaderBinder(entityHolder.getEntityTypes(), entityTableBinder, collectionLoaderBinder,
                        jdbcTemplate, dmlQueries, proxyFactory);
        this.entityPersisterBinder =
                new EntityPersisterBinder(entityHolder.getEntityTypes(), entityTableBinder, collectionLoaderBinder,
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

    public CollectionLoader getCollectionLoader(Class<?> entityType, String columnName) {
        return collectionLoaderBinder.getCollectionLoader(entityType, columnName);
    }

    public CollectionPersister getCollectionPersister(Class<?> entityType, String columnName) {
        return collectionPersisterBinder.getCollectionPersister(entityType, columnName);
    }
}
