package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import database.DatabaseServer;
import database.H2;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.impl.EntityManagerFactory;
import persistence.sql.ddl.generator.CreateDDLQueryGenerator;
import persistence.sql.ddl.generator.DropDDLQueryGenerator;
import persistence.sql.ddl.generator.fixture.PersonV3;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.Database;
import persistence.sql.dml.JdbcTemplate;
import persistence.sql.dml.statement.InsertStatementBuilder;
import persistence.sql.schema.meta.EntityClassMappingMeta;
import registry.EntityMetaRegistry;

@DisplayName("EntityManager 통합 테스트")
class EntityManagerImplIntegrationTest {

    private static DatabaseServer server;
    private static EntityManagerFactory entityManagerFactory;
    private static EntityMetaRegistry entityMetaRegistry;
    private static final Class<?> testClazz = PersonV3.class;
    private static Database jdbcTemplate;
    private EntityManager entityManager;

    @BeforeAll
    static void setServer() throws SQLException {
        server = new H2();
        server.start();
        Connection connection = server.getConnection();
        entityMetaRegistry = EntityMetaRegistry.of(new H2Dialect());
        entityMetaRegistry.addEntityMeta(testClazz);
        entityManagerFactory = new EntityManagerFactory(connection, entityMetaRegistry);
    }

    @AfterAll
    static void closeServer() {
        server.stop();
    }

    @BeforeEach
    void setUp() throws SQLException {
        entityManager = entityManagerFactory.createEntityManager();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        CreateDDLQueryGenerator createDDLQueryGenerator = new CreateDDLQueryGenerator();
        jdbcTemplate.execute(createDDLQueryGenerator.create(entityMetaRegistry.getEntityMeta(testClazz)));
    }

    @AfterEach
    void tearDown() {
        DropDDLQueryGenerator dropDDLQueryGenerator = new DropDDLQueryGenerator();
        jdbcTemplate.execute(dropDDLQueryGenerator.drop(PersonV3.class));
    }

    @Test
    @DisplayName("EntityManager를 통해 ID로 원하는 Entity를 갖고 올 수 있다.")
    void selectFindById() {
        //given
        InsertStatementBuilder insertStatementBuilder = new InsertStatementBuilder();
        PersonV3 person1 = new PersonV3("유저1", 20, "user1@jpa.com", 1);
        PersonV3 person2 = new PersonV3("유저2", 21, "user2@jpa.com", 2);
        PersonV3 person3 = new PersonV3("유저3", 25, "user3@jpa.com", 3);
        PersonV3 person4 = new PersonV3("유저4", 29, "user4@jpa.com", 4);

        final EntityClassMappingMeta entityClassMappingMeta = entityMetaRegistry.getEntityMeta(testClazz);
        final String person1Insert = insertStatementBuilder.insert(person1, entityClassMappingMeta);
        final String person2Insert = insertStatementBuilder.insert(person2, entityClassMappingMeta);
        final String person3Insert = insertStatementBuilder.insert(person3, entityClassMappingMeta);
        final String person4Insert = insertStatementBuilder.insert(person4, entityClassMappingMeta);

        jdbcTemplate.execute(person1Insert);
        jdbcTemplate.execute(person2Insert);
        jdbcTemplate.execute(person3Insert);
        jdbcTemplate.execute(person4Insert);

        final PersonV3 expectedPerson = entityManager.find(PersonV3.class, 1L);
        assertAll(
            () -> assertThat(expectedPerson.getId()).isEqualTo(1L),
            () -> assertThat(expectedPerson.getAge()).isEqualTo(20),
            () -> assertThat(expectedPerson.getEmail()).isEqualTo("user1@jpa.com"),
            () -> assertThat(expectedPerson.getIndex()).isEqualTo(null)
        );
    }

    @Test
    @DisplayName("EntityManager를 통해 Entity를 저장할 수 있다.")
    void persistEntity() {
        //given
        PersonV3 person1 = new PersonV3("유저1", 20, "user1@jpa.com", 1);

        //when
        entityManager.persist(person1);

        //then
        final PersonV3 expectedPerson = entityManager.find(PersonV3.class, 1L);
        assertAll(
            () -> assertThat(expectedPerson.getId()).isEqualTo(1L),
            () -> assertThat(expectedPerson.getAge()).isEqualTo(20),
            () -> assertThat(expectedPerson.getEmail()).isEqualTo("user1@jpa.com"),
            () -> assertThat(expectedPerson.getIndex()).isEqualTo(null)
        );
    }

    @Test
    @DisplayName("EntityManager를 통해 Entity를 삭제할 수 있다.")
    void deleteEntity() {
        //given
        PersonV3 person1 = new PersonV3("유저1", 20, "user1@jpa.com", 1);
        entityManager.persist(person1);
        final PersonV3 expectedPerson = entityManager.find(PersonV3.class, 1L);

        //expect
        assertThatCode(() -> entityManager.remove(expectedPerson))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("EntityManager를 통해 Entity를 수정할 수 있다.")
    void updateEntity() {
        //given
        final String userName = "유저1";
        final int userAge = 20;
        PersonV3 person1 = new PersonV3(userName, userAge, "user1@jpa.com", 1);
        entityManager.persist(person1);
        final PersonV3 expectedPerson = entityManager.find(PersonV3.class, 1L);

        //when
        final String updatedEmail = "updatedUser1@jpa.com";
        expectedPerson.updateEmail(updatedEmail);
        entityManager.persist(expectedPerson);

        //then
        final PersonV3 updatedPerson = entityManager.find(PersonV3.class, 1L);
        assertAll(
            () -> assertThat(updatedPerson.getId()).isEqualTo(1L),
            () -> assertThat(updatedPerson.getEmail()).isEqualTo(updatedEmail),
            () -> assertThat(updatedPerson.getName()).isEqualTo(userName),
            () -> assertThat(updatedPerson.getAge()).isEqualTo(userAge)
        );

    }
}