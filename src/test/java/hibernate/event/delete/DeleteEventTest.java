package hibernate.event.delete;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DeleteEventTest {

    @Test
    void DeleteEvent를_생성한다() {
        TestEntity givenEntity = new TestEntity();
        DeleteEvent<TestEntity> actual = DeleteEvent.createEvent(givenEntity);
        assertAll(
                () -> assertThat(actual.getEntity()).isEqualTo(givenEntity),
                () -> assertThat(actual.getClazz()).isEqualTo(TestEntity.class)
        );
    }

    @Entity
    private static class TestEntity {
        @Id
        private Long id;
    }
}
