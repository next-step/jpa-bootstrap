package boot;

import boot.metamodel.MetaModel;
import boot.metamodel.MyMetaModel;
import event.EventListenerGroup;
import jdbc.JdbcTemplate;
import persistence.entity.EntityManager;
import persistence.entity.MyEntityManager;

public class MyEntityManagerFactory implements EntityManagerFactory {
    private final EntityManagerContext entityManagerContext;
    private final MetaModel metaModel;

    public MyEntityManagerFactory(JdbcTemplate jdbcTemplate) {
        this.entityManagerContext = new MyEntityManagerContext();
        this.metaModel = new MyMetaModel(jdbcTemplate);
    }

    @Override
    public EntityManager openEntityManager() {
        if (entityManagerContext.currentEntityManager() != null) {
            throw new IllegalStateException("CurrentSessionContext exists already");
        }
        EventListenerGroup eventListenerGroup = EventListenerGroup.createDefaultGroup(metaModel);
        MyEntityManager entityManager = new MyEntityManager(metaModel, eventListenerGroup);
        entityManagerContext.bindEntityManager(entityManager);
        return entityManager;
    }

    @Override
    public EntityManager getCurrentEntityManager() {
        if (entityManagerContext.currentEntityManager() == null) {
            throw new IllegalStateException("No CurrentSessionContext configured");
        }
        return entityManagerContext.currentEntityManager();
    }
}
