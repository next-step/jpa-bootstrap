package hibernate;

import boot.Metamodel;
import boot.MetamodelImpl;
import builder.dml.builder.DMLQueryBuilder;
import event.EventListenerRegistry;
import event.action.ActionQueue;
import jdbc.JdbcTemplate;
import persistence.*;

public class EntityManagerFactoryImpl implements EntityManagerFactory {

    private final CurrentSessionContext currentSessionContext;
    private final JdbcTemplate jdbcTemplate;
    private final Metamodel metamodel;
    private final DMLQueryBuilder dmlQueryBuilder;

    public EntityManagerFactoryImpl(CurrentSessionContext currentSessionContext, JdbcTemplate jdbcTemplate, DMLQueryBuilder dmlQueryBuilder) {
        this.currentSessionContext = currentSessionContext;
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryBuilder = dmlQueryBuilder;

        this.metamodel = new MetamodelImpl(this.jdbcTemplate);
        this.metamodel.init();
    }

    @Override
    public EntityManager openSession() {
        EntityManager entityManager = createEntityManager();

        currentSessionContext.bind(entityManager);

        return entityManager;
    }

    @Override
    public void closeSession() {
        currentSessionContext.unbind();
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    private EntityManager createEntityManager() {
        return new EntityManagerImpl(createPersistenceContext(), metamodel, createEventListenerRegistry());
    }

    private PersistenceContext createPersistenceContext() {
        return new PersistenceContextImpl();
    }

    private EventListenerRegistry createEventListenerRegistry() {
        return new EventListenerRegistry(new ActionQueue(), metamodel, new EntityLoader(jdbcTemplate, dmlQueryBuilder));
    }

}
