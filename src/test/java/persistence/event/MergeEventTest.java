package persistence.event;

import domain.FixtureEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class MergeEventTest {

    @Test
    @DisplayName("merge event 를 생성 할 수 있다.")
    void mergeEventCreateTest() {
        final FixtureEntity.WithId entity = new FixtureEntity.WithId();
        final MergeEvent<FixtureEntity.WithId> mergeEvent = new MergeEvent<>(entity, 1L);

        assertSoftly(softly -> {
            softly.assertThat(mergeEvent.getTarget()).isEqualTo(entity);
            softly.assertThat(mergeEvent.getTargetClass()).isEqualTo(FixtureEntity.WithId.class);
            softly.assertThat(mergeEvent.getTargetId()).isEqualTo(1L);
        });
    }
}
