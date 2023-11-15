package persistence.entity.manager;

import jdbc.JdbcTemplate;
import persistence.core.EntityMetadataProvider;
import persistence.core.PersistenceEnvironment;
import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersisters;
import persistence.entity.proxy.EntityProxyFactory;
import persistence.sql.dml.DmlGenerator;

public class SimpleEntityManagerFactory implements EntityManagerFactory {
    private final EntityMetadataProvider entityMetadataProvider;
    private final EntityPersisters entityPersisters;
    private final EntityLoaders entityLoaders;
    private final EntityProxyFactory entityProxyFactory;


    public SimpleEntityManagerFactory(final EntityMetadataProvider entityMetadataProvider, final PersistenceEnvironment persistenceEnvironment) {
        this.entityMetadataProvider = entityMetadataProvider;
        final DmlGenerator dmlGenerator = persistenceEnvironment.getDmlGenerator();
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(persistenceEnvironment.getConnection());
        this.entityPersisters = new EntityPersisters(entityMetadataProvider, dmlGenerator, jdbcTemplate);
        this.entityLoaders = new EntityLoaders(entityMetadataProvider, dmlGenerator, jdbcTemplate);
        this.entityProxyFactory = new EntityProxyFactory(entityLoaders);
    }

    @Override
    public EntityManager createEntityManager() {
        return new SimpleEntityManager(entityMetadataProvider, entityPersisters, entityLoaders, entityProxyFactory);
    }
}

