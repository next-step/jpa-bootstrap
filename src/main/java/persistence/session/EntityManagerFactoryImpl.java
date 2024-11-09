package persistence.session;

import database.DatabaseServer;
import jdbc.JdbcTemplate;
import persistence.entity.StatefulPersistenceContext;
import persistence.meta.MetamodelInitializer;

import java.sql.SQLException;

public class EntityManagerFactoryImpl implements EntityManagerFactory {

    private static final ThreadLocal<EntityManager> sessionHolder = new ThreadLocal<>();

    private final CurrentSessionContext currentSessionContext;
    private final DatabaseServer server;

    public EntityManagerFactoryImpl(CurrentSessionContext currentSessionContext,
                                    DatabaseServer server) {
        this.currentSessionContext = currentSessionContext;
        this.server = server;
    }

    @Override
    public EntityManager openSession() throws SQLException {
        final EntityManager currentSession = currentSessionContext.currentSession();
        if (currentSession != null) {
            throw new IllegalStateException("Session already opened");
        }

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
        final MetamodelInitializer metamodelInitializer = new MetamodelInitializer(jdbcTemplate);

        final EntityManager newSession = new EntityManagerImpl(
                new JdbcTemplate(server.getConnection()),
                new StatefulPersistenceContext(),
                metamodelInitializer.getMetamodel()
        );

        sessionHolder.set(newSession);
        return newSession;
    }
}
