package hibernate.event.merge;

import database.DatabaseServer;
import database.H2;
import hibernate.action.ActionQueue;
import hibernate.action.EntityUpdateAction;
import hibernate.ddl.CreateQueryBuilder;
import hibernate.entity.meta.EntityClass;
import hibernate.metamodel.BasicMetaModel;
import hibernate.metamodel.MetaModel;
import hibernate.metamodel.MetaModelImpl;
import jakarta.persistence.*;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleMergeEventListenerTest {

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private static ActionQueue actionQueue;
    private static MetaModel metaModel;
    private static final Queue<EntityUpdateAction<?>> updateActionQueue = new LinkedList<>();

    @BeforeAll
    static void beforeAll() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        metaModel = MetaModelImpl.createPackageMetaModel(
                BasicMetaModel.createPackageMetaModel("hibernate.event.merge"),
                jdbcTemplate
        );
        actionQueue = new ActionQueue(
                new LinkedList<>(),
                updateActionQueue,
                new LinkedList<>()
        );

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
    void Event를_받아_merge_action을_저장한다() {
        // given
        jdbcTemplate.execute("insert into test_entity (id, nick_name) values (1, '최진영');");

        String expectedChangedName = "영진최";
        MergeEvent<TestEntity> mergeEvent = new MergeEvent<>(actionQueue, TestEntity.class, 1L,
                Map.of(new EntityClass<>(TestEntity.class).getEntityColumns().get(1), expectedChangedName));
        MergeEventListener mergeEventListener = new SimpleMergeEventListener(metaModel);

        // when
        mergeEventListener.onMerge(mergeEvent);

        // then
        assertThat(updateActionQueue).hasSize(1);
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
