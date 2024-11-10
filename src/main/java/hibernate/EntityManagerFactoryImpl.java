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

    public EntityManagerFactoryImpl(CurrentSessionContext currentSessionContext, JdbcTemplate jdbcTemplate) {
        this.currentSessionContext = currentSessionContext;
        this.jdbcTemplate = jdbcTemplate;
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
        Metamodel metamodel = new MetamodelImpl(this.jdbcTemplate);
        metamodel.init();

        return new EntityManagerImpl(new PersistenceContextImpl(), this.jdbcTemplate, metamodel);
    }
}
