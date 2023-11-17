package persistence.core;

import jdbc.JdbcTemplate;
import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersisters;
import persistence.exception.PersistenceException;
import persistence.sql.dml.DmlGenerator;

import java.sql.Connection;
import java.sql.SQLException;

public class MetaModelFactory {
    private final EntityMetadataProvider entityMetadataProvider;
    private final PersistenceEnvironment persistenceEnvironment;

    public MetaModelFactory(final EntityScanner entityScanner,
                            final PersistenceEnvironment persistenceEnvironment) {
        this.entityMetadataProvider = EntityMetadataProvider.from(entityScanner);
        this.persistenceEnvironment = persistenceEnvironment;
    }

    public MetaModel createMetaModel() {
        try (final Connection connection = persistenceEnvironment.getConnection()) {
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
            final DmlGenerator dmlGenerator = persistenceEnvironment.getDmlGenerator();
            final EntityPersisters entityPersisters = new EntityPersisters(entityMetadataProvider, dmlGenerator, jdbcTemplate);
            final EntityLoaders entityLoaders = new EntityLoaders(entityMetadataProvider, dmlGenerator, jdbcTemplate);
            return new MetaModelImpl(entityMetadataProvider, entityPersisters, entityLoaders);
        } catch (final SQLException e) {
            throw new PersistenceException("커넥션 연결을 실패했습니다.", e);
        }
    }
}
