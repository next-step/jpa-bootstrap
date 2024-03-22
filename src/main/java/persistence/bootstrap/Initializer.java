package persistence.bootstrap;

import database.dialect.Dialect;
import jdbc.JdbcTemplate;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerImpl;

import java.io.IOException;
import java.util.List;

public class Initializer {

    private final String basePackage;
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private List<Class<?>> components;
    private MetadataImpl metadata;

    public Initializer(String basePackage, JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.basePackage = basePackage;
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;

        this.components = null;
    }

    public void bootUp() {
        scanComponents();
        buildMetadata();
    }

    private void scanComponents() {
        if (components == null) {
            ComponentScanner componentScanner = new ComponentScanner();

            try {
                components = componentScanner.scan(basePackage);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void buildMetadata() {
        MetadataInitializer metadataInitializer = new MetadataInitializer(components);
        metadata = metadataInitializer.initialize(jdbcTemplate, dialect);
    }

    public EntityManager newEntityManager() {
        return EntityManagerImpl.from(metadata);
    }
}
