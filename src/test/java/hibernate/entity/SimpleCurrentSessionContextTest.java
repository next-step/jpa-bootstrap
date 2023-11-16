package hibernate.entity;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleCurrentSessionContextTest {

    @Test
    void 현재_보유중인_entityManager_세션이_없는_경우_null이_반환된다() {
        SimpleCurrentSessionContext simpleCurrentSessionContext = new SimpleCurrentSessionContext();
        EntityManager actual = simpleCurrentSessionContext.currentSession();
        assertThat(actual).isNull();
    }

    @Test
    void 현재_보유중인_entityManager_세션이_있는_경우_가져온다() {
        SimpleCurrentSessionContext simpleCurrentSessionContext = new SimpleCurrentSessionContext(Map.of(Thread.currentThread(), new EntityManagerImpl(null, null)));
        EntityManager actual = simpleCurrentSessionContext.currentSession();
        assertThat(actual).isNotNull();
    }
}
