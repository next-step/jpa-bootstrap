package persistence.session;

import jdbc.JdbcTemplate;
import persistence.action.ActionQueue;
import persistence.entity.StatefulPersistenceContext;
import persistence.event.EventListenerGroupHandler;
import persistence.meta.Metadata;
import persistence.meta.Metamodel;

import java.sql.SQLException;

public class SessionFactoryImpl implements EntityManagerFactory {

    private final CurrentSessionContext currentSessionContext;
    private final Metadata metadata;

    public SessionFactoryImpl(CurrentSessionContext currentSessionContext,
                              Metadata metadata) throws SQLException {
        this.currentSessionContext = currentSessionContext;
        this.metadata = metadata;

        // schema generation
        SchemaManagementToolCoordinator.processCreateTable(
                new JdbcTemplate(metadata.getDatabase().getConnection()),
                metadata
        );
    }

    @Override
    public EntityManager openSession() throws SQLException {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(metadata.getDatabase().getConnection());

        final EntityManager newSession = new SessionImpl(
                new StatefulPersistenceContext(),
                new Metamodel(metadata, jdbcTemplate),
                new EventListenerGroupHandler(),
                new ActionQueue()
        );

        currentSessionContext.bindSession(newSession);
        return newSession;
    }

    @Override
    public void close() throws SQLException {
        SchemaManagementToolCoordinator.processDropTable(
                new JdbcTemplate(metadata.getDatabase().getConnection()),
                metadata
        );
        currentSessionContext.closeSession();
    }
}
