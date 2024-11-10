package persistence.session;

import jdbc.JdbcTemplate;
import persistence.entity.StatefulPersistenceContext;
import persistence.meta.Metadata;
import persistence.meta.Metamodel;

import java.sql.SQLException;

public class EntityManagerFactoryImpl implements EntityManagerFactory {

    private final CurrentSessionContext currentSessionContext;
    private final Metadata metadata;

    public EntityManagerFactoryImpl(CurrentSessionContext currentSessionContext,
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

        final EntityManager newSession = new EntityManagerImpl(
                jdbcTemplate,
                new StatefulPersistenceContext(),
                new Metamodel(metadata, jdbcTemplate)
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
