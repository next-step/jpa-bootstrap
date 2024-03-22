package persistence.bootstrap;

import database.dialect.Dialect;
import jdbc.JdbcTemplate;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerImpl;
import persistence.entity.context.PersistentClass;

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
        this.metadata = null;
    }

    public void bootUp() {
        scanComponents();
        buildMetadata();
    }

    public void createTables() {
        for (Class<?> clazz : components) {
            PersistentClass<?> persistentClass = metadata.getPersistentClass(clazz);
            jdbcTemplate.execute(persistentClass.createQuery(dialect));
        }
    }

    private void scanComponents() {
        components = new ComponentScanner().scan(basePackage);
    }

    private void buildMetadata() {
        metadata = new MetadataInitializer(components).initialize(jdbcTemplate, dialect);
    }

    public EntityManager newEntityManager() {
        return EntityManagerImpl.newEntityManager(metadata);
    }
}
