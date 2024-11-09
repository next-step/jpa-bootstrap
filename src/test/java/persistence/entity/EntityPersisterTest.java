package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.Metamodel;
import persistence.meta.MetamodelInitializer;
import persistence.session.EntityManager;
import persistence.session.EntityManagerImpl;
import persistence.sql.H2Dialect;
import persistence.sql.ddl.query.CreateTableQueryBuilder;
import persistence.sql.ddl.query.DropQueryBuilder;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityPersisterTest {

    @Entity
    public static class QueryTestEntityWithIdentityId {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "nick_name", length = 60)
        private String name;

        private Integer age;

        public QueryTestEntityWithIdentityId() {
        }

        public QueryTestEntityWithIdentityId(Long id) {
            this.id = id;
        }

        public QueryTestEntityWithIdentityId(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private static Metamodel metamodel;
    private static EntityPersister entityPersister;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        metamodel = new MetamodelInitializer(jdbcTemplate).getMetamodel();
        entityPersister = new EntityPersister(metamodel.findTableDefinition(QueryTestEntityWithIdentityId.class), jdbcTemplate);

        jdbcTemplate.execute(new CreateTableQueryBuilder(new H2Dialect(), QueryTestEntityWithIdentityId.class, metamodel, List.of()).build());
    }

    @AfterEach
    void tearDown() throws SQLException {
        String query2 = new DropQueryBuilder(QueryTestEntityWithIdentityId.class, metamodel).build();

        jdbcTemplate.execute(query2);
        server.stop();
    }

    @Test
    void testGetEntityId() {
        QueryTestEntityWithIdentityId entityWithId1 = new QueryTestEntityWithIdentityId(1L);
        QueryTestEntityWithIdentityId entityWithId0 = new QueryTestEntityWithIdentityId(0L);

        assertAll(
                () -> assertThat(entityPersister.getEntityId(entityWithId1)).isEqualTo(1L),
                () -> assertThat(entityPersister.getEntityId(entityWithId0)).isEqualTo(0L)
        );
    }

    @Test
    @DisplayName("identity 전략 + id값이 null인 경우 정상적으로 insert되어야 한다.")
    void testInsert() {
        QueryTestEntityWithIdentityId entity = new QueryTestEntityWithIdentityId(null, "John", 25);

        entityPersister.insert(entity);

        EntityManager em = new EntityManagerImpl(jdbcTemplate, new StatefulPersistenceContext(), metamodel);
        QueryTestEntityWithIdentityId saved = em.find(QueryTestEntityWithIdentityId.class, 1L);
        assertAll(
                () -> assertThat(saved.id).isEqualTo(1L),
                () -> assertThat(saved.name).isEqualTo("John"),
                () -> assertThat(saved.age).isEqualTo(25)
        );
    }

    @Test
    void shouldExecuteInsertWithNullValue() {
        QueryTestEntityWithIdentityId entity = new QueryTestEntityWithIdentityId(1L);

        entityPersister.insert(entity);

        EntityManager em = new EntityManagerImpl(jdbcTemplate, new StatefulPersistenceContext(), metamodel);
        QueryTestEntityWithIdentityId saved = em.find(QueryTestEntityWithIdentityId.class, 1L);
        assertAll(
                () -> assertThat(saved.id).isEqualTo(1L),
                () -> assertThat(saved.name).isNull(),
                () -> assertThat(saved.age).isNull()
        );
    }

    @Test
    void shouldExecuteUpdate() {
        QueryTestEntityWithIdentityId entity = new QueryTestEntityWithIdentityId(1L, "John", 25);
        entityPersister.insert(entity);

        QueryTestEntityWithIdentityId updatedEntity = new QueryTestEntityWithIdentityId(1L, "Chanho", 30);

        entityPersister.update(updatedEntity);

        EntityManager em = new EntityManagerImpl(jdbcTemplate, new StatefulPersistenceContext(), metamodel);
        QueryTestEntityWithIdentityId updated = em.find(QueryTestEntityWithIdentityId.class, 1L);

        assertAll(
                () -> assertThat(updated.id).isEqualTo(1L),
                () -> assertThat(updated.name).isEqualTo("Chanho"),
                () -> assertThat(updated.age).isEqualTo(30)
        );
    }

    @Test
    void shouldExecuteDelete() {
        QueryTestEntityWithIdentityId entity = new QueryTestEntityWithIdentityId(1L);

        entityPersister.insert(entity);
        entityPersister.delete(entity);

        EntityManager em = new EntityManagerImpl(jdbcTemplate, new StatefulPersistenceContext(), metamodel);
        assertThrows(RuntimeException.class, () -> em.find(QueryTestEntityWithIdentityId.class, 1L));
    }
}
