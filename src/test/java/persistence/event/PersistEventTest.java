package persistence.event;

import domain.FixtureEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class PersistEventTest {


    @Test
    @DisplayName("persist event 를 생성 할 수 있다.")
    void persistEventCreateTest() {
        final FixtureEntity.WithId entity = new FixtureEntity.WithId();
        final PersistEvent<FixtureEntity.WithId> persistEvent = new PersistEvent<>(entity);

        assertSoftly(softly -> {
            softly.assertThat(persistEvent.getTarget()).isEqualTo(entity);
            softly.assertThat(persistEvent.getTargetClass()).isEqualTo(FixtureEntity.WithId.class);
            softly.assertThat(persistEvent.getTargetId()).isEqualTo(null);
        });
    }
}
