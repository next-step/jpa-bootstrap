package persistence.entity.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import database.DatabaseServer;
import database.H2;
import jakarta.persistence.Entity;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityManager;
import persistence.entity.impl.context.DefaultPersistenceContext;
import persistence.entity.impl.event.EntityEventDispatcher;
import persistence.entity.impl.event.EntityEventPublisher;
import persistence.entity.impl.event.dispatcher.EntityEventDispatcherImpl;
import persistence.entity.impl.event.listener.DeleteEntityEventListenerImpl;
import persistence.entity.impl.event.listener.FlushEntityEventListenerImpl;
import persistence.entity.impl.event.listener.LoadEntityEventListenerImpl;
import persistence.entity.impl.event.listener.MergeEntityEventListenerImpl;
import persistence.entity.impl.event.listener.PersistEntityEventListenerImpl;
import persistence.entity.impl.event.publisher.EntityEventPublisherImpl;
import persistence.entity.impl.retrieve.EntityLoader;
import persistence.entity.impl.retrieve.EntityLoaderImpl;
import persistence.entity.impl.store.EntityPersister;
import persistence.entity.impl.store.EntityPersisterImpl;
import persistence.sql.ddl.generator.CreateDDLQueryGenerator;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.Database;
import persistence.sql.dml.JdbcTemplate;
import registry.EntityMetaRegistry;

@DisplayName("EntityManager Flush 통합 테스트")
class EntityManagerFlushImplIntegrationTest {

    private EntityManager entityManager;

    private static EntityMetaRegistry entityMetaRegistry;
    private static final Class<?> testClazz = FlushEventEntity.class;

    private static DatabaseServer server;
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
        final EntityPersister persister = new EntityPersisterImpl(connection, entityMetaRegistry);
        final EntityLoader loader = new EntityLoaderImpl(connection, entityMetaRegistry);
        final DefaultPersistenceContext persistenceContext = new DefaultPersistenceContext(entityMetaRegistry);

        EntityEventPublisher entityEventPublisher = initEntityEventPublisher(loader, persister);

        entityManager = EntityManagerImpl.of(connection, persistenceContext, entityEventPublisher, entityMetaRegistry, FlushModeType.COMMIT);
        jdbcTemplate = new JdbcTemplate(connection);
        CreateDDLQueryGenerator createDDLQueryGenerator = new CreateDDLQueryGenerator();
        jdbcTemplate.execute(createDDLQueryGenerator.create(entityMetaRegistry.getEntityMeta(testClazz)));
    }

    private static EntityEventPublisher initEntityEventPublisher(EntityLoader loader, EntityPersister persister) {
        EntityEventDispatcher entityEventDispatcher = new EntityEventDispatcherImpl(
            new LoadEntityEventListenerImpl(loader),
            new MergeEntityEventListenerImpl(persister),
            new PersistEntityEventListenerImpl(persister),
            new DeleteEntityEventListenerImpl(persister),
            new FlushEntityEventListenerImpl()
        );

        return new EntityEventPublisherImpl(entityEventDispatcher);
    }

    @Test
    @DisplayName("EntityManager에 Flush를 수행하면 쓰기 지연된 쿼리가 수행된다.")
    void flushEvent() {
        // given
        final FlushEventEntity kevin = new FlushEventEntity(1L, "kevin");
        final FlushEventEntity chris = new FlushEventEntity(2L, "chris");
        final FlushEventEntity bob = new FlushEventEntity(3L, "bob");

        // when
        entityManager.persist(kevin);
        entityManager.persist(chris);
        entityManager.persist(bob);

        // then
        assertThatCode(() -> entityManager.flush())
            .doesNotThrowAnyException();
    }

    @Entity
    static class FlushEventEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        public FlushEventEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        protected FlushEventEntity() {
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
