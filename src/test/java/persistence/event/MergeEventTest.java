package persistence.event;

import domain.FixtureEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class MergeEventTest {

    @Test
    @DisplayName("merge event 를 생성 할 수 있다.")
    void persistEventCreateTest() {
        final FixtureEntity.WithId entity = new FixtureEntity.WithId();
        final MergeEvent mergeEvent = new MergeEvent(entity);

        assertSoftly(softly -> {
            softly.assertThat(mergeEvent.getTarget()).isEqualTo(entity);
            softly.assertThat(mergeEvent.getTargetClass()).isEqualTo(FixtureEntity.WithId.class);
        });
    }
}
