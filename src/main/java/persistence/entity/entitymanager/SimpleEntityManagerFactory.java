package persistence.entity.entitymanager;


import database.DatabaseServer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import jdbc.JdbcTemplate;
import persistence.sql.meta.MetaModel;
import persistence.sql.meta.SimpleMetaModel;

public class SimpleEntityManagerFactory implements EntityManagerFactory {

    private final SessionContext sessionContext;
    private final DatabaseServer databaseServer;
    private final List<Class<?>> entityClasses;

    public SimpleEntityManagerFactory(List<Class<?>> entityClasses, DatabaseServer databaseServer) {
        this.databaseServer = databaseServer;
        this.sessionContext = ThreadLocalSessionContext.getInstance();
        this.entityClasses = entityClasses;
    }

    @Override
    public EntityManager openSession() {
        Connection connection = getConnection();
        MetaModel metaModel = SimpleMetaModel.of(new JdbcTemplate(connection), entityClasses);

        sessionContext.bindSession(connection, SimpleEntityManager.from(metaModel));

        return sessionContext.currentSession();
    }

    private Connection getConnection() {
        try {
            return databaseServer.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
