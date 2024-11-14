package persistence.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SessionServiceTest {

    @Test
    void checkEventListenerGroup() {
        SessionService sessionService = new SessionService();
        assertAll(
                () -> assertThat(sessionService.PERSIST.getEventType()).isEqualTo(EventType.PERSIST),
                () -> assertThat(sessionService.PERSIST.getListeners()).hasSize(2),
                () -> assertThat(sessionService.LOAD.getEventType()).isEqualTo(EventType.LOAD),
                () -> assertThat(sessionService.LOAD.getListeners()).hasSize(1),
                () -> assertThat(sessionService.MERGE.getEventType()).isEqualTo(EventType.MERGE),
                () -> assertThat(sessionService.MERGE.getListeners()).hasSize(1),
                () -> assertThat(sessionService.DELETE.getEventType()).isEqualTo(EventType.DELETE),
                () -> assertThat(sessionService.DELETE.getListeners()).hasSize(1),
                () -> assertThat(sessionService.FLUSH.getEventType()).isEqualTo(EventType.FLUSH),
                () -> assertThat(sessionService.FLUSH.getListeners()).hasSize(1)
        );
    }
}
