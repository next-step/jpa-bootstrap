package persistence.event;

import domain.FixtureEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class DeleteEventTest {

    @Test
    @DisplayName("delete event 를 생성 할 수 있다.")
    void deleteEventCreateTest() {
        final FixtureEntity.WithId entity = new FixtureEntity.WithId();
        final DeleteEvent<FixtureEntity.WithId> deleteEvent = new DeleteEvent<>(entity, 1L);

        assertSoftly(softly -> {
            softly.assertThat(deleteEvent.getTarget()).isEqualTo(entity);
            softly.assertThat(deleteEvent.getTargetClass()).isEqualTo(FixtureEntity.WithId.class);
            softly.assertThat(deleteEvent.getTargetId()).isEqualTo(1L);
        });
    }

}
