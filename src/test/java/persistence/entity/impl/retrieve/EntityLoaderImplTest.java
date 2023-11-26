package persistence.entity.impl.retrieve;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import database.DatabaseServer;
import database.H2;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityManager;
import persistence.entity.impl.EntityManagerFactory;
import persistence.sql.ddl.generator.CreateDDLQueryGenerator;
import persistence.sql.ddl.generator.DropDDLQueryGenerator;
import persistence.sql.dialect.H2ColumnType;
import persistence.sql.dml.Database;
import persistence.sql.dml.JdbcTemplate;
import registry.EntityMetaRegistry;

@DisplayName("EntityLoader 테스트")
class EntityLoaderImplTest {

    private EntityManager entityManager;

    private static DatabaseServer server;
    private static EntityMetaRegistry entityMetaRegistry;
    private static final Class<?> testClazz = EntityLoaderEntity.class;
    private static Database jdbcTemplate;
    private static Connection connection;

    @BeforeAll
    static void setServer() throws SQLException {
        server = new H2();
        server.start();
        connection = server.getConnection();
        entityMetaRegistry = EntityMetaRegistry.of(new H2ColumnType());
        entityMetaRegistry.addEntityMeta(testClazz);
    }

    @BeforeEach
    void setUp() {
        final EntityManagerFactory emf = new EntityManagerFactory(connection, entityMetaRegistry);
        entityManager = emf.createEntityManager();
        jdbcTemplate = new JdbcTemplate(connection);
        CreateDDLQueryGenerator createDDLQueryGenerator = new CreateDDLQueryGenerator();
        jdbcTemplate.execute(createDDLQueryGenerator.create(entityMetaRegistry.getEntityMeta(testClazz)));
    }

    @AfterEach
    void tearDown() {
        DropDDLQueryGenerator dropDDLQueryGenerator = new DropDDLQueryGenerator();
        jdbcTemplate.execute(dropDDLQueryGenerator.drop(testClazz));
    }

    @Test
    @DisplayName("EntityLoader를 통해 Entity를 불러올 수 있다.")
    void entityLoaderCanLoad() throws SQLException {
        final EntityLoaderEntity entity = new EntityLoaderEntity();
        final EntityLoaderEntity loadedEntity = (EntityLoaderEntity) entityManager.persist(entity);

        final EntityLoaderImpl entityLoader = new EntityLoaderImpl(server.getConnection(), entityMetaRegistry);

        final EntityLoaderEntity load = entityLoader.load(EntityLoaderEntity.class, loadedEntity.getId());
        assertAll(
            () -> assertThat(load.getId()).isEqualTo(loadedEntity.getId())
        );
    }

    @Entity
    static class EntityLoaderEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        public EntityLoaderEntity(Long id) {
            this.id = id;
        }

        protected EntityLoaderEntity() {
        }

        public Long getId() {
            return id;
        }
    }
}
