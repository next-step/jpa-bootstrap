package persistence.entity.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import jakarta.persistence.Entity;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Connection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import persistence.sql.dialect.H2Dialect;
import registry.EntityMetaRegistry;

@DisplayName("EntityManager Flush 단위 테스트")
class EntityManagerFlushImplTest {

    private EntityManager entityManager;

    private static EntityMetaRegistry entityMetaRegistry;
    private static final Class<?> testClazz = FlushEventEntity.class;

    @Mock
    private static Connection connection;

    private final EntityPersister persister = mock(EntityPersister.class);

    @BeforeAll
    static void beforeAll() {
        entityMetaRegistry = EntityMetaRegistry.of(new H2Dialect());
        entityMetaRegistry.addEntityMeta(testClazz);
    }

    @BeforeEach
    void setUp() {
        given(persister.store(any())).willReturn(
            new FlushEventEntity(1L, "kevin"),
            new FlushEventEntity(2L, "chris"),
            new FlushEventEntity(3L, "bob")
        );
        final EntityLoaderImpl loader = new EntityLoaderImpl(connection, entityMetaRegistry);
        final DefaultPersistenceContext persistenceContext = new DefaultPersistenceContext(entityMetaRegistry);

        final EntityEventPublisher entityEventPublisher = initEntityEventPublisher(loader, persister);

        entityManager = EntityManagerImpl.of(connection, persistenceContext, entityEventPublisher, entityMetaRegistry,
            FlushModeType.COMMIT);
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
    @DisplayName("flush가 호출되기 전에 쿼리가 발생하지 않는다.")
    void flushEvent() {
        // given
        final FlushEventEntity kevin = new FlushEventEntity(1L, "kevin");
        final FlushEventEntity chris = new FlushEventEntity(2L, "chris");
        final FlushEventEntity bob = new FlushEventEntity(3L, "bob");

        // when
        entityManager.persist(kevin);
        entityManager.persist(chris);
        entityManager.persist(bob);

        then(persister).shouldHaveNoInteractions();
        entityManager.flush();

        // then
        then(persister).should(times(3)).store(any());
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
