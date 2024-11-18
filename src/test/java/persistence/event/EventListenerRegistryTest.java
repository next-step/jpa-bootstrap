package persistence.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import util.ReflectionUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class EventListenerRegistryTest {
    @ParameterizedTest
    @MethodSource("testParameters")
    @DisplayName("이벤트 리스터 그룹을 반환한다.")
    void getEventListenerGroup(EventType<?> eventType, int expected) {
        // given
        final EventListenerRegistry eventListenerRegistry = new EventListenerRegistry();

        // when
        final EventListenerGroup<?> eventListenerGroup = eventListenerRegistry.getEventListenerGroup(eventType);

        // then
        assertAll(
                () -> assertThat(eventListenerGroup.getEventType()).isEqualTo(eventType),
                () -> assertThat(getListeners(eventListenerGroup)).hasSize(expected)
        );
    }

    private <T> List<T> getListeners(EventListenerGroup<T> eventListenerGroup) throws NoSuchFieldException, IllegalAccessException {
        return (List<T>) ReflectionUtils.getFieldValue(eventListenerGroup, "listeners");
    }

    private static Stream<Arguments> testParameters() {
        return Stream.of(
                Arguments.of(EventType.LOAD, 1),
                Arguments.of(EventType.PERSIST, 1),
                Arguments.of(EventType.DELETE, 1),
                Arguments.of(EventType.UPDATE, 1),
                Arguments.of(EventType.DIRTY_CHECK, 1),
                Arguments.of(EventType.MERGE, 1),
                Arguments.of(EventType.FLUSH, 1),
                Arguments.of(EventType.CLEAR, 1)
        );
    }
}
