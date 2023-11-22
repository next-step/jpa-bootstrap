package hibernate.event.merge;

import hibernate.entity.meta.EntityClass;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MergeEventTest {

    @Test
    void MergeEvent를_생성한다() {
        EntityClass<TestEntity> testEntityClass = new EntityClass<>(TestEntity.class);
        MergeEvent<TestEntity> actual = MergeEvent.createEvent(new TestEntity(1L, "최진영"),
                testEntityClass.getEntityId(), Map.of(testEntityClass.getEntityColumns().get(1), "진영최"));
        assertAll(
                () -> assertThat(actual.getClazz()).isEqualTo(TestEntity.class),
                () -> assertThat(actual.getEntityId()).isEqualTo(1L),
                () -> assertThat(actual.getChangeColumns()).isNotNull()
        );
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
