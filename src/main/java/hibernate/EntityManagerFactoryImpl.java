package hibernate;

import boot.Metamodel;
import boot.MetamodelImpl;
import builder.dml.builder.DMLQueryBuilder;
import jdbc.JdbcTemplate;
import persistence.EntityManager;
import persistence.EntityManagerImpl;
import persistence.PersistenceContextImpl;

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
        return new EntityManagerImpl(jdbcTemplate, new PersistenceContextImpl(), metamodel, dmlQueryBuilder);
    }
}
