package hibernate.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static hibernate.event.EventListenerRegistry.createDefaultRegistry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventListenerRegistryTest {

    @Test
    void 없는_EventType의_Listener를_받으려하는_경우_예외가_발생한다() {
        EventListenerRegistry eventListenerRegistry = new EventListenerRegistry(Map.of());
        assertThatThrownBy(() -> eventListenerRegistry.getListener(EventType.LOAD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 EventType에 일치하는 EventListener가 없습니다.");
    }

    @ParameterizedTest
    @CsvSource(value = {
            "LOAD,hibernate.event.load.SimpleLoadEventListener",
            "PERSIST,hibernate.event.persist.SimplePersistEventListener",
            "MERGE,hibernate.event.merge.SimpleMergeEventListener",
            "DELETE,hibernate.event.delete.SimpleDeleteEventListener"
    })
    void createDefaultRegistry로_EventType에_맞는_Listener를_가져온다(final EventType eventType, final String expectedClassName) throws ClassNotFoundException {
        EventListenerRegistry defaultRegistry = createDefaultRegistry(null, null);
        EventListener<?> actual = defaultRegistry.getListener(eventType);
        assertThat(actual.getValue()).isInstanceOf(Class.forName(expectedClassName));
    }
}
