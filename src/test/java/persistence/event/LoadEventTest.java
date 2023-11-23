package persistence.event;

import domain.FixtureEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class LoadEventTest {


    @Test
    @DisplayName("load event 를 생성 할 수 있다.")
    void loadEventCreateTest() {
        final LoadEvent<FixtureEntity.Person> loadEvent = new LoadEvent<>(1L, FixtureEntity.Person.class);

        assertSoftly(softly->{
            softly.assertThat(loadEvent.getTargetClass()).isEqualTo(FixtureEntity.Person.class);
            softly.assertThat(loadEvent.getTargetId()).isEqualTo(1L);
            softly.assertThat(loadEvent.getTarget()).isEqualTo(null);
        });
    }
}
