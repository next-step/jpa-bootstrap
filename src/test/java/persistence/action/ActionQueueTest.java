package persistence.action;

import database.DatabaseServer;
import database.H2;
import domain.FixtureEntity.Person;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import org.junit.jupiter.api.*;
import persistence.Application;
import persistence.core.*;
import persistence.dialect.h2.H2Dialect;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisters;
import persistence.sql.ddl.DdlGenerator;
import persistence.sql.dml.DmlGenerator;
import persistence.util.ReflectionUtils;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ActionQueueTest {
    private static EntityMetadataProvider entityMetadataProvider;
    private static DmlGenerator dmlGenerator;
    private static DdlGenerator ddlGenerator;
    private static JdbcTemplate jdbcTemplate;
    private static EntityMetadata<Person> personEntityMetadata;
    private EntityPersister entityPersister;

    @BeforeAll
    static void integrationBeforeAll() throws SQLException {
        final DatabaseServer server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        final PersistenceEnvironment persistenceEnvironment = new PersistenceEnvironment(server, new H2Dialect());
        final EntityScanner entityScanner = new EntityScanner(Application.class);
        entityMetadataProvider = EntityMetadataProvider.from(entityScanner);
        final MetaModelFactory metaModelFactory = new MetaModelFactory(entityScanner, persistenceEnvironment);
        ddlGenerator = new DdlGenerator(metaModelFactory.createMetaModel(), persistenceEnvironment.getDialect());
        dmlGenerator = new DmlGenerator(persistenceEnvironment.getDialect());
        personEntityMetadata = entityMetadataProvider.getEntityMetadata(Person.class);
        final String createPersonDdl = ddlGenerator.generateCreateDdl(personEntityMetadata);
        jdbcTemplate.execute(createPersonDdl);
        insertDummyPerson("테스터1", 10, "test1@test.com");
        insertDummyPerson("테스터2", 20, "test2@test.com");
        insertDummyPerson("테스터3", 30, "test3@test.com");
        insertDummyPerson("테스터4", 40, "test4@test.com");
        insertDummyPerson("테스터5", 50, "test5@test.com");
    }

    @AfterAll
    static void afterAll() {
        final String dropPersonDdl = ddlGenerator.generateDropDdl(personEntityMetadata);
        jdbcTemplate.execute(dropPersonDdl);
    }

    @BeforeEach
    void setUp() {
        final EntityPersisters entityPersisters = new EntityPersisters(entityMetadataProvider, dmlGenerator, jdbcTemplate);
        entityPersister = entityPersisters.getEntityPersister(Person.class);
    }

    @Test
    @DisplayName("addInsertion 은 현재 바로 execute 된다.")
    void addInsertionTest() {
        final ActionQueue actionQueue = new ActionQueue();
        final Person entity = new Person("종민", 30, "jongmin4943@gmail.com");

        actionQueue.addInsertion(new EntityInsertAction(entityPersister, entity));

        final Person result = selectPerson(6L);
        assertSoftly(softly -> {
            softly.assertThat(result.getId()).isEqualTo(6L);
            softly.assertThat(result.getName()).isEqualTo("종민");
            softly.assertThat(result.getAge()).isEqualTo(30);
            softly.assertThat(result.getEmail()).isEqualTo("jongmin4943@gmail.com");
        });
    }

    @Test
    @DisplayName("addUpdate 는 execute 시 반영된다.")
    void addUpdateExecuteTest() {
        final ActionQueue actionQueue = new ActionQueue();
        final Person entity = new Person(1L, "변경", 100, "modify@modify.com");

        actionQueue.addUpdate(new EntityUpdateAction(entityPersister, entity));

        actionQueue.executeUpdate();

        final Person result = selectPerson(1L);
        assertSoftly(softly -> {
            softly.assertThat(result.getId()).isEqualTo(1L);
            softly.assertThat(result.getName()).isEqualTo("변경");
            softly.assertThat(result.getAge()).isEqualTo(100);
            softly.assertThat(result.getEmail()).isEqualTo("modify@modify.com");
        });
    }

    @Test
    @DisplayName("addUpdate 는 execute 하지 않으면 반영되지 않는다.")
    void addUpdateNotExecuteTest() {
        final ActionQueue actionQueue = new ActionQueue();
        final Person entity = new Person(2L, "변경", 100, "modify@modify.com");

        actionQueue.addUpdate(new EntityUpdateAction(entityPersister, entity));

        final Person result = selectPerson(2L);
        assertSoftly(softly -> {
            softly.assertThat(result.getId()).isEqualTo(2L);
            softly.assertThat(result.getName()).isEqualTo("테스터2");
            softly.assertThat(result.getAge()).isEqualTo(20);
            softly.assertThat(result.getEmail()).isEqualTo("test2@test.com");
        });
    }

    @Test
    @DisplayName("addUpdate 는 같은 entity에 대한 Action 이 여러번 쌓이면 마지막에 넣어진 Action 만 반영한다.")
    void addUpdateLastActionOnlyExecuteTest() {
        final ActionQueue actionQueue = new ActionQueue();
        final Person entity = new Person(3L, "변경", 100, "modify@modify.com");
        actionQueue.addUpdate(new EntityUpdateAction(entityPersister, entity));
        entity.changeEmail("modify2@modify.com");
        actionQueue.addUpdate(new EntityUpdateAction(entityPersister, entity));
        entity.changeEmail("modify3@modify.com");
        actionQueue.addUpdate(new EntityUpdateAction(entityPersister, entity));

        actionQueue.executeUpdate();

        final Person result = selectPerson(3L);
        assertSoftly(softly -> {
            softly.assertThat(result.getId()).isEqualTo(3L);
            softly.assertThat(result.getName()).isEqualTo("변경");
            softly.assertThat(result.getAge()).isEqualTo(100);
            softly.assertThat(result.getEmail()).isEqualTo("modify3@modify.com");
        });
    }

    @Test
    @DisplayName("addDelete 는 execute 시 반영된다.")
    void addDeleteExecuteTest() {
        final ActionQueue actionQueue = new ActionQueue();
        final Person entity = new Person(4L, "변경", 100, "modify@modify.com");

        actionQueue.addDeletion(new EntityDeleteAction(entityPersister, entity));

        actionQueue.executeDelete();

        assertThatThrownBy(() -> selectPerson(4L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Data not exist");
    }

    @Test
    @DisplayName("addDelete 는 execute 하지 않으면 반영되지 않는다.")
    void addDeleteNotExecuteTest() {
        final ActionQueue actionQueue = new ActionQueue();
        final Person entity = new Person(5L, "변경", 100, "modify@modify.com");

        actionQueue.addDeletion(new EntityDeleteAction(entityPersister, entity));

        final Person result = selectPerson(5L);
        assertThat(result).isNotNull();
    }


    private Person selectPerson(final Long id) {
        final String query = dmlGenerator.findById(personEntityMetadata.getTableName(), personEntityMetadata.toColumnNames(), personEntityMetadata.getIdColumnName(), id);
        return jdbcTemplate.queryForObject(query, personRowMapper());
    }

    private RowMapper<Person> personRowMapper() {
        return rs -> new Person(rs.getLong("id"), rs.getString("nick_name"), rs.getInt("old"), rs.getString("email"));
    }

    private static void insertDummyPerson(final String name, final int age, final String email) {
        final Person entity = new Person(name, age, email);
        final List<String> columnNames = personEntityMetadata.toInsertableColumnNames();
        final List<Object> values = ReflectionUtils.getFieldValues(entity, personEntityMetadata.toInsertableColumnFieldNames());
        jdbcTemplate.execute(dmlGenerator.insert(personEntityMetadata.getTableName(), columnNames, values));
    }
}
