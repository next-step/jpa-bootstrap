package hibernate.event.load;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LoadEventTest {

    @Test
    void LoadEvent를_생성한다() {
        LoadEvent<TestEntity> actual = new LoadEvent<>(TestEntity.class, 1L);
        assertAll(
                () -> assertThat(actual.getEntityId()).isEqualTo(1L),
                () -> assertThat(actual.getClazz()).isEqualTo(TestEntity.class)
        );
    }

    @Entity
    private static class TestEntity {
        @Id
        private Long id;
    }
}
