package hibernate;

import boot.Metamodel;
import boot.MetamodelImpl;
import jdbc.JdbcTemplate;
import persistence.EntityManager;
import persistence.EntityManagerImpl;
import persistence.PersistenceContextImpl;

public class EntityManagerFactoryImpl implements EntityManagerFactory {

    private final CurrentSessionContext currentSessionContext;
    private final JdbcTemplate jdbcTemplate;
    private final Metamodel metamodel;

    public EntityManagerFactoryImpl(CurrentSessionContext currentSessionContext, JdbcTemplate jdbcTemplate) {
        this.currentSessionContext = currentSessionContext;
        this.jdbcTemplate = jdbcTemplate;

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
        return new EntityManagerImpl(new PersistenceContextImpl(), this.jdbcTemplate, metamodel);
    }
}
