package hibernate.event.merge;

import database.DatabaseServer;
import database.H2;
import hibernate.action.ActionQueue;
import hibernate.action.EntityInsertAction;
import hibernate.action.EntityUpdateAction;
import hibernate.ddl.CreateQueryBuilder;
import hibernate.entity.meta.EntityClass;
import hibernate.metamodel.BasicMetaModel;
import hibernate.metamodel.MetaModel;
import hibernate.metamodel.MetaModelImpl;
import jakarta.persistence.*;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleMergeEventListenerTest {

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private ActionQueue actionQueue;
    private MetaModel metaModel;
    private Queue<EntityInsertAction<?>> insertActions;
    private Queue<EntityUpdateAction<?>> updateActionQueue;

    @BeforeAll
    static void beforeAll() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(CreateQueryBuilder.INSTANCE.generateQuery(new EntityClass<>(TestEntity.class)));
    }

    @BeforeEach
    void setUp() {
        metaModel = MetaModelImpl.createPackageMetaModel(
                BasicMetaModel.createPackageMetaModel("hibernate.event.merge"),
                jdbcTemplate
        );
        insertActions = new LinkedList<>();
        updateActionQueue = new LinkedList<>();
        actionQueue = new ActionQueue(
                insertActions,
                updateActionQueue,
                new LinkedList<>()
        );
    }

    @AfterEach
    void afterEach() {
        jdbcTemplate.execute("truncate table test_entity;");
    }

    @AfterAll
    static void afterAll() {
        jdbcTemplate.execute("drop table test_entity;");
        server.stop();
    }

    @Test
    void Event를_받아_merge_action을_저장한다() {
        // given
        jdbcTemplate.execute("insert into test_entity (id, nick_name) values (1, '최진영');");

        String expectedChangedName = "영진최";
        EntityClass<TestEntity> testEntityClass = new EntityClass<>(TestEntity.class);
        MergeEvent<TestEntity> mergeEvent = MergeEvent.createEvent(new TestEntity(1L, expectedChangedName),
                testEntityClass.getEntityId(),
                Map.of(testEntityClass.getEntityColumns().get(1), expectedChangedName));
        MergeEventListener mergeEventListener = new SimpleMergeEventListener(metaModel, actionQueue);

        // when
        mergeEventListener.onMerge(mergeEvent);

        // then
        assertThat(updateActionQueue).hasSize(1);
    }

    @Test
    void changedColumns이_null_인_경우_identity_insert_action을_저장한다() {
        // given
        MergeEvent<TestEntity> mergeEvent = MergeEvent.createEvent(new TestEntity("최진영"), new EntityClass<>(TestEntity.class).getEntityId(), null);
        MergeEventListener mergeEventListener = new SimpleMergeEventListener(metaModel, actionQueue);

        // when
        mergeEventListener.onMerge(mergeEvent);
        TestEntity actual = findTestEntity();

        // then
        assertThat(actual.name).isEqualTo("최진영");
    }

    private TestEntity findTestEntity() {
        return jdbcTemplate.queryForObject("select id, nick_name, from test_entity", new RowMapper<TestEntity>() {
            @Override
            public TestEntity mapRow(ResultSet resultSet) throws SQLException {
                return new TestEntity(
                        resultSet.getLong("id"),
                        resultSet.getString("nick_name")
                );
            }
        });
    }

    @Entity
    @Table(name = "test_entity")
    private static class TestEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "nick_name")
        private String name;

        @Transient
        private String email;

        public TestEntity() {
        }

        public TestEntity(String name) {
            this.name = name;
        }

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
