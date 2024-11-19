package event;

import boot.Metamodel;
import boot.MetamodelImpl;
import builder.dml.builder.DMLQueryBuilder;
import database.H2DBConnection;
import event.action.ActionQueue;
import event.listener.persist.PersistEventListenerImpl;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.EntityLoader;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventListenerRegistryTest {

    private H2DBConnection h2DBConnection;

    //정확한 테스트를 위해 메소드마다 테이블 DROP 후 DB종료
    @AfterEach
    void tearDown() {
        this.h2DBConnection.stop();
    }

    @DisplayName("EventType에 맞지 않는 Listener를 추가하면 예외가 발생한다.")
    @Test
    void addListenerThrowExceptionTest() {
        this.h2DBConnection = new H2DBConnection();
        JdbcTemplate jdbcTemplate = this.h2DBConnection.start();

        Metamodel metamodel = new MetamodelImpl(jdbcTemplate);
        metamodel.init();

        ActionQueue actionQueue = new ActionQueue();
        EventListenerRegistry eventListenerRegistry = EventListenerRegistry.createEventListenerRegistry(metamodel, new EntityLoader(jdbcTemplate, new DMLQueryBuilder()), actionQueue);

        assertThatThrownBy(() -> eventListenerRegistry.addListener(EventType.LOAD, new PersistEventListenerImpl(metamodel, actionQueue)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Listener의 타입이 맞지 않습니다.interface event.listener.load.LoadEventListener");
    }

}
