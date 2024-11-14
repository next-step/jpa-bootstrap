package persistence.bootstrap;

import jdbc.JdbcTemplate;
import persistence.bootstrap.binder.EntityAssociationBinder;
import persistence.bootstrap.binder.EntityBinder;
import persistence.bootstrap.binder.EntityTableBinder;
import persistence.dialect.Dialect;
import persistence.entity.manager.CurrentSessionContext;
import persistence.entity.manager.factory.EntityManagerFactory;
import persistence.event.EventListenerRegistry;

import java.sql.Connection;

public class Metadata {
    private final JdbcTemplate jdbcTemplate;
    private final EntityTableBinder entityTableBinder;
    private final Metamodel metamodel;

    public Metadata(Connection connection, Dialect dialect, String... basePackages) {
        final EntityBinder entityBinder = new EntityBinder(basePackages);
        final EntityTableBinder entityTableBinder = new EntityTableBinder(entityBinder.getEntityTypes());
        final EntityAssociationBinder entityAssociationBinder = new EntityAssociationBinder(entityTableBinder);

        this.jdbcTemplate = new JdbcTemplate(connection);
        this.entityTableBinder = entityTableBinder;
        this.metamodel = new Metamodel(jdbcTemplate, entityBinder, entityTableBinder, new EventListenerRegistry());

        DatabaseSyncManager.sync(entityTableBinder, entityAssociationBinder, new JdbcTemplate(connection), dialect);
    }

    public Metamodel getMetamodel() {
        return metamodel;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        final CurrentSessionContext currentSessionContext = new CurrentSessionContext();
        return new EntityManagerFactory(currentSessionContext, metamodel);
    }

    public void close() {
        DatabaseSyncManager.clear(entityTableBinder, jdbcTemplate);

        entityTableBinder.clear();
        metamodel.close();
    }
}
