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

        this.metamodel = new MetamodelImpl(this.jdbcTemplate);
        this.metamodel.init();

        this.dmlQueryBuilder = dmlQueryBuilder;
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
        ActionQueue actionQueue = new ActionQueue();
        return new EntityManagerImpl(
                createPersistenceContext(),
                metamodel,
                EventListenerRegistry.createEventListenerRegistry(metamodel, new EntityLoader(jdbcTemplate, dmlQueryBuilder), actionQueue),
                actionQueue
        );
    }

    private PersistenceContext createPersistenceContext() {
        return new PersistenceContextImpl();
    }

}
