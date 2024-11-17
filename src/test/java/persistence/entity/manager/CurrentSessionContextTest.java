package persistence.entity.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.Metadata;
import util.TestHelper;

import static org.assertj.core.api.Assertions.*;

class CurrentSessionContextTest {
    private Metadata metadata;

    @BeforeEach
    void setUp() {
        metadata = TestHelper.createMetadata("fixture");
    }

    @AfterEach
    void tearDown() {
        metadata.close();
    }

    @Test
    @DisplayName("세션이 오픈된 상태에서 오픈을 요청하면 예외를 발생한다.")
    void openSession_exception() {
        // given
        final CurrentSessionContext currentSessionContext = new CurrentSessionContext();
        final EntityManager entityManager = metadata.getEntityManagerFactory().openSession();
        currentSessionContext.openSession(entityManager);

        // when & then
        assertThatThrownBy(() -> currentSessionContext.openSession(new DefaultEntityManager(metadata.getMetamodel())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(CurrentSessionContext.SESSION_ALREADY_CREATED_MESSAGE);

    }
}
