package persistence.action;

import fixture.EntityWithId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.Metadata;
import persistence.bootstrap.Metamodel;
import persistence.entity.manager.EntityManager;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.event.dirtycheck.DirtyCheckEvent;
import persistence.event.dirtycheck.DirtyCheckEventListener;
import util.ReflectionUtils;
import util.TestHelper;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ActionQueueTest {
    private final EntityWithId defaultEntity = new EntityWithId("Jaden", 30, "test@email.com", 1);
    private final EntityWithId entityForPersist = new EntityWithId("Yang", 35, "test2@email.com", 1);
    private Metadata metadata;

    @BeforeEach
    void setUp() {
        metadata = TestHelper.createMetadata("fixture");
    }

    @AfterEach
    void tearDown() {
        metadata.close();
    }

    @Test
    @DisplayName("ActionQueue의 모든 action을 실행한다.")
    void executeAll() throws NoSuchFieldException, IllegalAccessException {
        // given
        final ActionQueue actionQueue = new ActionQueue();
        final EntityManager entityManager = metadata.getEntityManagerFactory().openSession();
        entityManager.persist(defaultEntity);
        actionQueue.addAction(createPersistAction());
        actionQueue.addAction(createUpdateAction());
        actionQueue.addAction(createDeleteAction());

        // when
        actionQueue.executeAll();
        entityManager.clear();

        // then
        final EntityWithId managedDefaultEntity = entityManager.find(EntityWithId.class, defaultEntity.getId());
        assertAll(
                () -> assertThat(managedDefaultEntity).isNotNull(),
                () -> assertThat(managedDefaultEntity.getId()).isNotNull(),
                () -> assertThat(managedDefaultEntity.getName()).isEqualTo("Jason"),
                () -> assertThat(managedDefaultEntity.getAge()).isEqualTo(20),
                () -> assertThat(managedDefaultEntity.getEmail()).isEqualTo("test1@email.com"),
                () -> assertThatThrownBy(() -> entityManager.find(EntityWithId.class, entityForPersist.getId()))
                        .isInstanceOf(RuntimeException.class)
        );
    }

    private PersistAction<EntityWithId> createPersistAction() throws NoSuchFieldException, IllegalAccessException {
        return new PersistAction<>(metadata.getMetamodel(), getPersistenceContext(), entityForPersist);
    }

    private UpdateAction<EntityWithId> createUpdateAction() throws NoSuchFieldException, IllegalAccessException {
        final Metamodel metamodel = metadata.getMetamodel();
        final EntityWithId updateEntity = new EntityWithId(defaultEntity.getId(), "Jason", 20, "test1@email.com");

        final DirtyCheckEvent<EntityWithId> dirtyCheckEvent = new DirtyCheckEvent<>(metamodel, defaultEntity, updateEntity);
        metamodel.getDirtyCheckEventListenerGroup().doEvent(dirtyCheckEvent, DirtyCheckEventListener::onDirtyCheck);
        return new UpdateAction<>(metamodel, getPersistenceContext(), updateEntity, dirtyCheckEvent.getResult());
    }

    private DeleteAction<EntityWithId> createDeleteAction() {
        return new DeleteAction<>(metadata.getMetamodel(), entityForPersist);
    }

    private PersistenceContext getPersistenceContext() throws NoSuchFieldException, IllegalAccessException {
        final EntityManager entityManager = metadata.getEntityManagerFactory().openSession();
        return (PersistenceContext) ReflectionUtils.getFieldValue(entityManager, "persistenceContext");
    }
}
