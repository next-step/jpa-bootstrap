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
    private final EventListenerRegistry<?> eventListenerRegistry;

    public EntityManagerFactoryImpl(CurrentSessionContext currentSessionContext, JdbcTemplate jdbcTemplate, DMLQueryBuilder dmlQueryBuilder) {
        this.currentSessionContext = currentSessionContext;
        this.jdbcTemplate = jdbcTemplate;

        this.metamodel = new MetamodelImpl(this.jdbcTemplate);
        this.metamodel.init();

        this.eventListenerRegistry = new EventListenerRegistry<>(metamodel, new EntityLoader(jdbcTemplate, dmlQueryBuilder));
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
        return new EntityManagerImpl(createPersistenceContext(), metamodel, this.eventListenerRegistry.addActionQueue(new ActionQueue()));
    }

    private PersistenceContext createPersistenceContext() {
        return new PersistenceContextImpl();
    }

}
