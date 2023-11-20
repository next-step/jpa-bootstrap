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
        final DeleteEvent deleteEvent = new DeleteEvent(entity);

        assertSoftly(softly -> {
            softly.assertThat(deleteEvent.getTarget()).isEqualTo(entity);
            softly.assertThat(deleteEvent.getTargetClass()).isEqualTo(FixtureEntity.WithId.class);
        });
    }

}
