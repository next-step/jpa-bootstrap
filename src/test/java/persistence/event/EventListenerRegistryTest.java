package persistence.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.action.ActionQueue;
import persistence.bootstrap.Metadata;
import persistence.entity.manager.factory.PersistenceContext;
import persistence.event.load.DefaultLoadEventListener;
import util.ReflectionUtils;
import util.TestHelper;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class EventListenerRegistryTest {
    private Metadata metadata;

    @BeforeEach
    void setUp() {
        metadata = TestHelper.createMetadata("domain");
    }

    @AfterEach
    void tearDown() {
        metadata.close();
    }

    @Test
    @DisplayName("이벤트 리스너를 등록한다.")
    void registerEventListener() throws NoSuchFieldException, IllegalAccessException {
        // given
        final PersistenceContext persistenceContext = new PersistenceContext();
        final EventListenerRegistry eventListenerRegistry =
                new EventListenerRegistry(metadata.getMetamodel(), persistenceContext, new ActionQueue());

        // when
        eventListenerRegistry.registerEventListener(
                EventType.LOAD, new DefaultLoadEventListener(metadata.getMetamodel(), persistenceContext));

        // then
        assertThat(getEventListeners(eventListenerRegistry)).hasSize(2);
    }

    private List<EventListener> getEventListeners(EventListenerRegistry eventListenerRegistry) throws NoSuchFieldException, IllegalAccessException {
        final Map<EventType<?>, EventListenerGroup<EventListener>> eventListenerGroupRegistry =
                (Map<EventType<?>, EventListenerGroup<EventListener>>) ReflectionUtils.getFieldValue(eventListenerRegistry, "eventListenerGroupRegistry");
        final EventListenerGroup<EventListener> eventListenerGroup = eventListenerGroupRegistry.get(EventType.LOAD);
        return (List<EventListener>) ReflectionUtils.getFieldValue(eventListenerGroup, "listeners");
    }
}
