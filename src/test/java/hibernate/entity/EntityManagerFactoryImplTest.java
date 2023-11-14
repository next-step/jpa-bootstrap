package hibernate.entity;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityManagerFactoryImplTest {

    @Test
    void 이미_세션을_열었는데_또_열려고하는_경우_예외가_발생한다() {
        CurrentSessionContext currentSessionContext = new SimpleCurrentSessionContext(
                Map.of(Thread.currentThread(), new EntityManagerImpl(null, null)));
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(currentSessionContext, null);
        assertThatThrownBy(entityManagerFactory::openSession)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 현재 스레드에 생성된 EntityManager가 있습니다.");
    }
}
