package persistence.entity.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.Metamodel;
import util.TestHelper;

import static org.assertj.core.api.Assertions.*;

class CurrentSessionContextTest {
    private Metamodel metamodel;

    @BeforeEach
    void setUp() {
        metamodel = TestHelper.createMetamodel("fixture");
    }

    @AfterEach
    void tearDown() {
        metamodel.close();
    }

    @Test
    @DisplayName("세션이 오픈된 상태에서 오픈을 요청하면 예외를 발생한다.")
    void openSession_exception() {
        // given
        final CurrentSessionContext currentSessionContext = new CurrentSessionContext();
        currentSessionContext.openSession(new DefaultEntityManager(metamodel));

        // when & then
        assertThatThrownBy(() -> currentSessionContext.openSession(new DefaultEntityManager(metamodel)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(CurrentSessionContext.SESSION_ALREADY_CREATED_MESSAGE);

    }
}
