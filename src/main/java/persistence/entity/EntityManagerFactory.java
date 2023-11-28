package persistence.entity;


import java.sql.Connection;
import persistence.meta.MetaModel;
import persistence.sql.QueryGenerator;


public class EntityManagerFactory {
    private final CurrentSessionContext currentSessionContext;
    private final MetaModel metaModel;
    private final QueryGenerator queryGenerator;

    public EntityManagerFactory(MetaModel metaModel, QueryGenerator queryGenerator, CurrentSessionContext currentSessionContext) {
        this.metaModel = metaModel;
        this.queryGenerator = queryGenerator;
        this.currentSessionContext = currentSessionContext;
    }

    public EntityManager openSession(Connection connection) {
        if (currentSessionContext.currentSession() == null) {
            final SimpleEntityManager simpleEntityManager = new SimpleEntityManager(metaModel, queryGenerator, connection);
            currentSessionContext.bind(simpleEntityManager);
        }
        return currentSessionContext.currentSession();
    }

    public void closeSession() {
        final EntityManager entityManager = currentSessionContext.currentSession();
        entityManager.flush();
        currentSessionContext.closeCurrentSession();
    }
}
