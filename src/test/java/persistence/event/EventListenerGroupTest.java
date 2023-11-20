package persistence.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventListenerGroupTest {

    static class TestEventListener implements EventListener {
        public String test() {
            return "test";
        }
    }

    @Test
    @DisplayName("fireEvent 를 통해 biConsumer 를 실행시킬 수 있다.")
    void fireEventTest() {
        final EventListenerGroup<TestEventListener> listenerGroup = new EventListenerGroup<>(new TestEventListener());

        listenerGroup.fireEvent("event", (s, s2) ->
            assertThat(s.test() + s2).isEqualTo("testevent")
        );
    }

    @Test
    @DisplayName("fireEvent 를 통해 biFunction 을 실행시켜 값을 반환 받을 수 있다.")
    void fireEventReturnTest() {
        final EventListenerGroup<TestEventListener> listenerGroup = new EventListenerGroup<>(new TestEventListener());

        final String result = listenerGroup.fireEventReturn("event", (s, s2) -> s.test() + s2);

        assertThat(result).isEqualTo("testevent");
    }
}
