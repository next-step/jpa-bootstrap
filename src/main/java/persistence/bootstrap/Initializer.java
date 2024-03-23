package persistence.bootstrap;

import database.dialect.Dialect;
import jdbc.JdbcTemplate;
import persistence.entitymanager.EntityManager;

import java.util.List;

public class Initializer {

    private final String basePackage;
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private List<Class<?>> entityClasses;
    private Metadata metadata;

    private boolean bootstrapped;
    private Metamodel metamodel;

    public Initializer(String basePackage, JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.basePackage = basePackage;
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;

        this.bootstrapped = false;

        this.entityClasses = null;
        this.metadata = null;
    }

    public void initialize() {
        scanComponents();
        buildMetadata();
        buildMetamodel();

        bootstrapped = true;
    }

    private void scanComponents() {
        entityClasses = new ComponentScanner().scan(basePackage);
    }

    private void buildMetadata() {
        metadata = new MetadataImpl(dialect);
        entityClasses.forEach(metadata::register);
    }

    private void buildMetamodel() {
        MetamodelImpl metamodelImpl = new MetamodelImpl(metadata, jdbcTemplate, dialect);
        metamodelImpl.initialize();
        metamodel = metamodelImpl;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    // 필요할 때만 생성할 수 있도록, 초기화 단계에서 실행하지 않습니다.
    public void createTables() {
        EntityManager entityManager = createEntityManagerFactory().openSession();
        for (Class<?> clazz : entityClasses) {
            entityManager.createTable(clazz);
        }
    }

    public EntityManagerFactory createEntityManagerFactory() {
        if (!bootstrapped) {
            throw new RuntimeException("아직 초기화가 되지 않았어요");
        }

        EntityManagerFactoryImpl entityManagerFactory = new EntityManagerFactoryImpl(metadata, metamodel);
        entityManagerFactory.initialize();
        return entityManagerFactory;
    }
}
