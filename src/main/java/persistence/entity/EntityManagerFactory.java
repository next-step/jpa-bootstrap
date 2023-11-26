package persistence.entity;


import java.sql.Connection;
import persistence.meta.MetaModel;


public class EntityManagerFactory {
    private final CurrentSessionContext currentSessionContext;
    private final MetaModel metaModel;

    public EntityManagerFactory(MetaModel metaModel) {
        this.metaModel = metaModel;
        this.currentSessionContext = new CurrentSessionContext();
    }

    public EntityManager openSession(Connection connection) {
        final SimpleEntityManager simpleEntityManager = new SimpleEntityManager(metaModel, connection);
        currentSessionContext.bind(simpleEntityManager);
        return simpleEntityManager;
    }

    public void closeCurrentSession() {
        currentSessionContext.closeCurrentSession();
    }
}
