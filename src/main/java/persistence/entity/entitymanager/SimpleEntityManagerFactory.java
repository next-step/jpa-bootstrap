package persistence.entity.entitymanager;


import database.DatabaseServer;
import java.util.List;
import jdbc.JdbcTemplate;
import persistence.sql.meta.MetaModel;
import persistence.sql.meta.SimpleMetaModel;

public class SimpleEntityManagerFactory implements EntityManagerFactory {

    private final SessionContext sessionContext;
    private final MetaModel metaModel;

    private static SimpleEntityManagerFactory instance;

    private SimpleEntityManagerFactory(List<Class<?>> entityClasses, DatabaseServer databaseServer) {
        this.sessionContext = ThreadLocalSessionContext.getInstance();
        this.metaModel = SimpleMetaModel.of(new JdbcTemplate(databaseServer), entityClasses);
    }

    public static synchronized SimpleEntityManagerFactory getInstance(List<Class<?>> entityClasses, DatabaseServer databaseServer) {
        if (instance == null) {
            instance = new SimpleEntityManagerFactory(entityClasses, databaseServer);
        }
        return instance;
    }

    @Override
    public EntityManager openSession() {
        sessionContext.bindSession(SimpleEntityManager.from(metaModel));

        return sessionContext.currentSession();
    }
}
