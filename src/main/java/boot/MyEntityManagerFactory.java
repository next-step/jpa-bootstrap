package boot;

import boot.metamodel.MetaModel;
import boot.metamodel.MyMetaModel;
import jdbc.JdbcTemplate;
import persistence.entity.EntityManager;
import persistence.entity.MyEntityManager;

public class MyEntityManagerFactory implements EntityManagerFactory {
    private final CurrentSessionContext currentSessionContext;
    private final MetaModel metaModel;

    public MyEntityManagerFactory(JdbcTemplate jdbcTemplate) {
        this.currentSessionContext = new MyCurrentSessionContext();
        this.metaModel = new MyMetaModel(jdbcTemplate);
    }

    @Override
    public EntityManager openSession() {
        if (currentSessionContext.currentSession() != null) {
            throw new IllegalStateException("CurrentSessionContext exists already");
        }
        MyEntityManager entityManager = new MyEntityManager(metaModel);
        currentSessionContext.bindSession(entityManager);
        return entityManager;
    }

    @Override
    public EntityManager getCurrentSession() {
        if (currentSessionContext.currentSession() == null) {
            throw new IllegalStateException("No CurrentSessionContext configured");
        }
        return currentSessionContext.currentSession();
    }
}
