package hibernate.event.persist;

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

import static org.assertj.core.api.Assertions.assertThat;

class SimplePersistEventListenerTest {

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private static EntitySource entitySource;

    @BeforeAll
    static void beforeAll() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        MetaModel metaModel = MetaModelImpl.createPackageMetaModel(
                BasicMetaModel.createPackageMetaModel("hibernate.event.persist"),
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
    void Event를_받아_persist한다() {
        // given
        TestEntity givenEntity = new TestEntity("최진영", 19);
        PersistEvent<TestEntity> persistEvent = PersistEvent.createEvent(entitySource, givenEntity);
        PersistEventListener persistEventListener = new SimplePersistEventListener();

        // when
        Object actual = persistEventListener.onPersist(persistEvent);

        // then
        assertThat(actual).isEqualTo(1L);

    }

    private Integer testEntityCount() {
        return jdbcTemplate.queryForObject("select count(*) from test_entity", new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet) {
                try {
                    return resultSet.getInt(1);
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

        public TestEntity(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
    }
}
