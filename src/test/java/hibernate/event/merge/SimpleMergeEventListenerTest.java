package hibernate.event.merge;

import database.DatabaseServer;
import database.H2;
import hibernate.action.ActionQueue;
import hibernate.ddl.CreateQueryBuilder;
import hibernate.entity.EntityManagerImpl;
import hibernate.entity.EntitySource;
import hibernate.entity.meta.EntityClass;
import hibernate.metamodel.BasicMetaModel;
import hibernate.metamodel.MetaModel;
import hibernate.metamodel.MetaModelImpl;
import jakarta.persistence.*;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleMergeEventListenerTest {

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private static EntitySource entitySource;

    @BeforeAll
    static void beforeAll() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        MetaModel metaModel = MetaModelImpl.createPackageMetaModel(
                BasicMetaModel.createPackageMetaModel("hibernate.event.merge"),
                jdbcTemplate
        );
        ActionQueue actionQueue = new ActionQueue();
        entitySource = new EntityManagerImpl(null, metaModel, null, actionQueue);

        jdbcTemplate.execute(CreateQueryBuilder.INSTANCE.generateQuery(new EntityClass<>(TestEntity.class)));
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
    void Event를_받아_merge한다() {
        // given
        jdbcTemplate.execute("insert into test_entity (id, nick_name) values (1, '최진영');");

        String expectedChangedName = "영진최";
        MergeEvent<TestEntity> mergeEvent = MergeEvent.createEvent(entitySource, TestEntity.class, 1L,
                Map.of(new EntityClass<>(TestEntity.class).getEntityColumns().get(1), expectedChangedName));
        MergeEventListener mergeEventListener = new SimpleMergeEventListener();

        // when
        mergeEventListener.onMerge(mergeEvent);
        TestEntity actual = testEntity();

        // then
        assertThat(actual.name).isEqualTo(expectedChangedName);
    }

    private TestEntity testEntity() {
        return jdbcTemplate.queryForObject("select id, nick_name, age from test_entity", new RowMapper<TestEntity>() {
            @Override
            public TestEntity mapRow(ResultSet resultSet) {
                try {
                    return new TestEntity(
                            resultSet.getLong("id"),
                            resultSet.getString("nick_name"),
                            resultSet.getInt("age")
                    );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
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

        private Integer age;

        @Transient
        private String email;

        public TestEntity() {
        }

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public TestEntity(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public TestEntity(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
    }
}
