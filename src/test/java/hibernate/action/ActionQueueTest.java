package hibernate.action;

import hibernate.entity.EntityPersister;
import hibernate.entity.meta.EntityClass;
import hibernate.entity.meta.column.EntityColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ActionQueueTest {

    @Test
    void IdentityInsertAction이_입력되면_기존의_insert_action을_모두_처리한다() {
        // given
        Queue<EntityInsertAction<?>> givenInsertions = new ConcurrentLinkedQueue<>();
        ActionQueue actionQueue = new ActionQueue(
                givenInsertions,
                new ConcurrentLinkedQueue<>(),
                new ConcurrentLinkedQueue<>()
        );
        actionQueue.addAction(new EntityBasicInsertAction<>(new MockEntityPersister<>(), null, null));
        actionQueue.addAction(new EntityBasicInsertAction<>(new MockEntityPersister<>(), null, null));

        // when
        actionQueue.addAction(new EntityIdentityInsertAction<>(
                new MockEntityPersister<>(), new TestEntity(1L, "최진영"), new EntityClass<>(TestEntity.class).getEntityId()));

        // then
        assertThat(givenInsertions).hasSize(0);
    }

    @Test
    void 동일한_id의_EntityBasicInsertAction이_입력되면_중복저장하지_않는다() {
        // given
        Queue<EntityInsertAction<?>> givenInsertions = new ConcurrentLinkedQueue<>();
        ActionQueue actionQueue = new ActionQueue(
                givenInsertions,
                new ConcurrentLinkedQueue<>(),
                new ConcurrentLinkedQueue<>()
        );
        TestEntity givenEntity = new TestEntity(1L, "최진영");
        actionQueue.addAction(new EntityBasicInsertAction<>(new MockEntityPersister<>(), givenEntity, 1L));

        // when
        actionQueue.addAction(new EntityBasicInsertAction<>(new MockEntityPersister<>(), givenEntity, 1L));

        // then
        assertThat(givenInsertions).hasSize(1);
    }

    @Test
    void 모든_action을_실행한다() {
        // given
        Queue<EntityInsertAction<?>> givenInsertions = new ConcurrentLinkedQueue<>();
        Queue<EntityUpdateAction<?>> givenUpdates = new ConcurrentLinkedQueue<>();
        Queue<EntityDeleteAction<?>> givenDeletions = new ConcurrentLinkedQueue<>();
        ActionQueue actionQueue = new ActionQueue(givenInsertions, givenUpdates, givenDeletions);

        actionQueue.addAction(new EntityBasicInsertAction<>(new MockEntityPersister<>(), null, null));
        actionQueue.addAction(new EntityUpdateAction<>(new MockEntityPersister<>(), null, null));
        actionQueue.addAction(new EntityDeleteAction<>(new MockEntityPersister<>(), null));

        // when
        actionQueue.executeAllActions();

        // then
        assertAll(
                () -> assertThat(givenInsertions).isEmpty(),
                () -> assertThat(givenUpdates).isEmpty(),
                () -> assertThat(givenDeletions).isEmpty()
        );
    }

    private static class MockEntityPersister<T> extends EntityPersister<T> {

        public MockEntityPersister() {
            super(null, null);
        }

        @Override
        public boolean update(Object entityId, Map<EntityColumn, Object> updateFields) {
            return true;
        }

        @Override
        public Object insert(Object entity) {
            return 1L;
        }

        @Override
        public void delete(Object entity) {
        }
    }

    @Entity
    private static class TestEntity {
        @Id
        private Long id;

        private String name;

        public TestEntity() {
        }

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
