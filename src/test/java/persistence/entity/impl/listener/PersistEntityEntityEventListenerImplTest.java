package persistence.entity.impl.listener;

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
import persistence.entity.EventSource;
import persistence.entity.impl.EntityManagerImpl;
import persistence.entity.impl.context.DefaultPersistenceContext;
import persistence.entity.impl.event.EntityEventDispatcher;
import persistence.entity.impl.event.EntityEventListener;
import persistence.entity.impl.event.EntityEventPublisher;
import persistence.entity.impl.event.dispatcher.EntityEventDispatcherImpl;
import persistence.entity.impl.event.listener.DeleteEntityEventListenerImpl;
import persistence.entity.impl.event.listener.LoadEntityEventListenerImpl;
import persistence.entity.impl.event.listener.MergeEntityEventListenerImpl;
import persistence.entity.impl.event.listener.PersistEntityEventListenerImpl;
import persistence.entity.impl.event.publisher.EntityEventPublisherImpl;
import persistence.entity.impl.event.type.PersistEntityEvent;
import persistence.entity.impl.retrieve.EntityLoaderImpl;
import persistence.entity.impl.store.EntityPersisterImpl;
import persistence.sql.ddl.generator.CreateDDLQueryGenerator;
import persistence.sql.ddl.generator.DropDDLQueryGenerator;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.Database;
import persistence.sql.dml.JdbcTemplate;
import registry.EntityMetaRegistry;

@DisplayName("PersistEventListener 테스트")
class PersistEntityEntityEventListenerImplTest {

    private EntityEventListener persistEntityEventListener;
    private EntityManager entityManager;
    private EventSource eventSource;

    private static DatabaseServer server;
    private static EntityMetaRegistry entityMetaRegistry;
    private static final Class<?> testClazz = PersistEventEntity.class;
    private static Database jdbcTemplate;
    private static Connection connection;

    @BeforeAll
    static void setServer() throws SQLException {
        server = new H2();
        server.start();
        connection = server.getConnection();
        entityMetaRegistry = EntityMetaRegistry.of(new H2Dialect());
        entityMetaRegistry.addEntityMeta(testClazz);
    }

    @BeforeEach
    void setUp() {
        final EntityPersisterImpl persister = new EntityPersisterImpl(connection, entityMetaRegistry);
        final EntityLoaderImpl loader = new EntityLoaderImpl(connection, entityMetaRegistry);
        persistEntityEventListener = new PersistEntityEventListenerImpl(persister);
        final DefaultPersistenceContext persistenceContext = new DefaultPersistenceContext(entityMetaRegistry);
        eventSource = persistenceContext;

        EntityEventDispatcher entityEventDispatcher = new EntityEventDispatcherImpl(
            new LoadEntityEventListenerImpl(loader),
            new MergeEntityEventListenerImpl(persister),
            persistEntityEventListener,
            new DeleteEntityEventListenerImpl(persister)
        );
        EntityEventPublisher entityEventPublisher = new EntityEventPublisherImpl(entityEventDispatcher);

        entityManager = new EntityManagerImpl(connection, persistenceContext, entityEventPublisher, entityMetaRegistry);
        jdbcTemplate = new JdbcTemplate(connection);
        CreateDDLQueryGenerator createDDLQueryGenerator = new CreateDDLQueryGenerator();
        jdbcTemplate.execute(createDDLQueryGenerator.create(entityMetaRegistry.getEntityMeta(testClazz)));
    }

    @AfterEach
    void tearDown() throws Exception {
        DropDDLQueryGenerator dropDDLQueryGenerator = new DropDDLQueryGenerator();
        jdbcTemplate.execute(dropDDLQueryGenerator.drop(testClazz));
        entityManager.close();
        server.stop();
    }

    @Test
    @DisplayName("삽입 이벤트를 수신하면 해당 엔티티는 적재된다.")
    void persistEvent() {
        // given
        final PersistEventEntity persistEventEntity = new PersistEventEntity();
        final PersistEntityEvent persistEvent = PersistEntityEvent.of(persistEventEntity, eventSource);

        // when
        final PersistEventEntity persistedEntity = persistEntityEventListener.onEvent(PersistEventEntity.class, persistEvent);

        // then
        final PersistEventEntity savedEntity = entityManager.find(PersistEventEntity.class, 1L);
        assertAll(
            () -> assertThat(savedEntity).isNotNull(),
            () -> {
                assert savedEntity != null;
                assertThat(persistedEntity.getId()).isEqualTo(savedEntity.getId());
            }
        );

    }

    @Entity
    static class PersistEventEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        public PersistEventEntity(Long id) {
            this.id = id;
        }

        protected PersistEventEntity() {
        }

        public Long getId() {
            return id;
        }
    }
}
