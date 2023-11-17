package hibernate.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventListenerTest {

    @Test
    void onload의_function을_실행한다() {
        String actual = new EventListener<>("test").fireOnLoad("event", (entity, event) -> "test complete " + event);
        assertThat(actual).isEqualTo("test complete event");
    }
}
