package persistence.entity.manager;

import database.DatabaseServer;
import jdbc.JdbcTemplate;
import persistence.context.PersistenceContext;
import persistence.context.PersistenceContextImpl;
import persistence.entity.attribute.EntityAttributes;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.SimpleEntityLoader;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.SimpleEntityPersister;

import java.sql.SQLException;

public class EntityManagerFactoryImpl implements EntityManagerFactory {
    private final CurrentSessionContext currentSessionContext = new CurrentSessionContext();
    private final DatabaseServer databaseServer;

    public EntityManagerFactoryImpl(DatabaseServer databaseServer) {
        this.databaseServer = databaseServer;
    }

    @Override
    public EntityManager openSession(DatabaseServer databaseServer) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(databaseServer.getConnection());
        EntityAttributes entityAttributes = new EntityAttributes();
        EntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate);

        EntityPersister entityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader, entityAttributes);

        PersistenceContext persistenceContext = new PersistenceContextImpl(entityPersister, entityAttributes);
        EntityManager entityManager = EntityManagerImpl.of(persistenceContext);
        currentSessionContext.setSession(entityManager);

        return entityManager;
    }

    @Override
    public EntityManager getSession(DatabaseServer databaseServer) throws SQLException {
        return currentSessionContext.getSession();
    }
}
