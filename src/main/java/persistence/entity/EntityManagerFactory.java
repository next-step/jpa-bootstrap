package persistence.entity;


import java.sql.Connection;

import persistence.dialect.Dialect;
import persistence.entity.binder.AnnotationBinder;
import persistence.meta.MetaModel;
import persistence.sql.QueryGenerator;


public class EntityManagerFactory {
    private final CurrentSessionContext currentSessionContext;
    private final MetaModel metaModel;
    private final QueryGenerator queryGenerator;

    private EntityManagerFactory(MetaModel metaModel, QueryGenerator queryGenerator, CurrentSessionContext currentSessionContext) {
        this.metaModel = metaModel;
        this.queryGenerator = queryGenerator;
        this.currentSessionContext = currentSessionContext;
    }

    public static EntityManagerFactory genrateThreadLocalEntityManagerFactory(ClassScanner scanner, Dialect dialect) {
        return new EntityManagerFactory(AnnotationBinder.bindMetaModel(scanner), QueryGenerator.of(dialect), new ThreadLocalSessionContext());
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
