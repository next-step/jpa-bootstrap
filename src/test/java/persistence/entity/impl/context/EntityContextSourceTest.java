package persistence.entity.impl.context;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityEntry;
import persistence.entity.ContextSource;
import persistence.entity.type.EntityStatus;
import persistence.sql.dialect.H2Dialect;
import registry.EntityMetaRegistry;

@DisplayName("EventSource 테스트")
class EntityContextSourceTest {

    private static EntityMetaRegistry entityMetaRegistry;

    private ContextSource contextSource;

    @BeforeAll
    static void setMetaRegistry() {
        entityMetaRegistry = EntityMetaRegistry.of(new H2Dialect());
        entityMetaRegistry.addEntityMeta(EventSourceTestEntity.class);
    }

    @BeforeEach
    void setUp() {
        contextSource = new DefaultPersistenceContext(entityMetaRegistry);
    }

    @Test
    @DisplayName("Entity를 MANAGED 상태로 설정할 수 있다.")
    void setEntityStatusManaged() {
        //given
        final EventSourceTestEntity entity = new EventSourceTestEntity(1L, "test");

        //when
        contextSource.managed(entity);

        //then
        final EntityEntry entityEntry = contextSource.getEntityEntry(entity);
        assertThat(entityEntry.getStatus()).isEqualTo(EntityStatus.MANAGED);
    }

    @Test
    @DisplayName("Entity를 GONE 상태로 설정할 수 있다.")
    void setEntityStatusGone() {
        //given
        final EventSourceTestEntity entity = new EventSourceTestEntity(1L, "test");

        //when
        contextSource.gone(entity);

        //then
        final EntityEntry entityEntry = contextSource.getEntityEntry(entity);
        assertThat(entityEntry.getStatus()).isEqualTo(EntityStatus.GONE);
    }

    @Test
    @DisplayName("Entity를 DELETED 상태로 설정할 수 있다.")
    void setEntityStatusDeleted() {
        //given
        final EventSourceTestEntity entity = new EventSourceTestEntity(1L, "test");

        //when
        contextSource.deleted(entity);

        //then
        final EntityEntry entityEntry = contextSource.getEntityEntry(entity);
        assertThat(entityEntry.getStatus()).isEqualTo(EntityStatus.DELETED);
    }

    @Test
    @DisplayName("Entity를 SAVING 상태로 설정할 수 있다.")
    void setEntityStatusSaving() {
        //given
        final EventSourceTestEntity entity = new EventSourceTestEntity(1L, "test");

        //when
        contextSource.saving(entity);

        //then
        final EntityEntry entityEntry = contextSource.getEntityEntry(entity);
        assertThat(entityEntry.getStatus()).isEqualTo(EntityStatus.SAVING);
    }

    @Test
    @DisplayName("Entity를 Loading 상태로 설정할 수 있다.")
    void setEntityStatusLoading() {
        //given
        final EventSourceTestEntity entity = new EventSourceTestEntity(1L, "test");

        //when
        contextSource.loading(entity);

        //then
        final EntityEntry entityEntry = contextSource.getEntityEntry(entity);
        assertThat(entityEntry.getStatus()).isEqualTo(EntityStatus.LOADING);
    }

    @Test
    @DisplayName("Entity를 READ_ONLY 상태로 설정할 수 있다.")
    void setEntityStatusReadOnly() {
        //given
        final EventSourceTestEntity entity = new EventSourceTestEntity(1L, "test");

        //when
        contextSource.readOnly(entity);

        //then
        final EntityEntry entityEntry = contextSource.getEntityEntry(entity);
        assertThat(entityEntry.getStatus()).isEqualTo(EntityStatus.READ_ONLY);
    }

    @Entity
    static class EventSourceTestEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        public EventSourceTestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        protected EventSourceTestEntity() {
        }
    }
}
