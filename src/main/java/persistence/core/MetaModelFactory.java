package persistence.core;

import jdbc.JdbcTemplate;
import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersisters;
import persistence.sql.dml.DmlGenerator;

import java.sql.Connection;

public class MetaModelFactory {
    private final EntityMetadataProvider entityMetadataProvider;
    private final PersistenceEnvironment persistenceEnvironment;

    public MetaModelFactory(final EntityScanner entityScanner,
                            final PersistenceEnvironment persistenceEnvironment) {
        this.entityMetadataProvider = EntityMetadataProvider.from(entityScanner);
        this.persistenceEnvironment = persistenceEnvironment;
    }

    public MetaModel createMetaModel() {
        final Connection connection = persistenceEnvironment.getConnection();
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
        final DmlGenerator dmlGenerator = persistenceEnvironment.getDmlGenerator();
        final EntityPersisters entityPersisters = new EntityPersisters(entityMetadataProvider, dmlGenerator, jdbcTemplate);
        final EntityLoaders entityLoaders = new EntityLoaders(entityMetadataProvider, dmlGenerator, jdbcTemplate);
        return new MetaModelImpl(entityMetadataProvider, entityPersisters, entityLoaders);
    }
}