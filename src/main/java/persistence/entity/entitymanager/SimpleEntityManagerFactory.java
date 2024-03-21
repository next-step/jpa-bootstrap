package persistence.entity.entitymanager;


import database.DatabaseServer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import jdbc.JdbcTemplate;
import jdbc.JdbcTemplatePool;
import persistence.sql.meta.MetaModel;
import persistence.sql.meta.SimpleMetaModel;

public class SimpleEntityManagerFactory implements EntityManagerFactory {

    private final SessionContext sessionContext;
    private final JdbcTemplatePool jdbcTemplatePool;
    private final List<Class<?>> entityClasses;

    private static SimpleEntityManagerFactory instance;

    private SimpleEntityManagerFactory(List<Class<?>> entityClasses, DatabaseServer databaseServer) throws SQLException {
        this.jdbcTemplatePool = JdbcTemplatePool.getInstance(databaseServer);
        this.sessionContext = ThreadLocalSessionContext.getInstance();
        this.entityClasses = entityClasses;
    }

    public static synchronized SimpleEntityManagerFactory getInstance(List<Class<?>> entityClasses, DatabaseServer databaseServer) throws SQLException {
        if (instance == null) {
            instance = new SimpleEntityManagerFactory(entityClasses, databaseServer);
        }
        return instance;
    }

    @Override
    public EntityManager openSession() {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        MetaModel metaModel = SimpleMetaModel.of(getJdbcTemplate(), entityClasses);

        sessionContext.bindSession(jdbcTemplate, SimpleEntityManager.from(metaModel));

        return sessionContext.currentSession();
    }

    private JdbcTemplate getJdbcTemplate() {
        try {
            return jdbcTemplatePool.getJdbcTemplate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
