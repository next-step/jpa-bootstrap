package hibernate.entity;

import hibernate.metamodel.BasicMetaModel;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityManagerFactoryImplTest {

    @Test
    void 이미_세션을_열었는데_또_열려고하는_경우_예외가_발생한다() {
        CurrentSessionContext currentSessionContext = new SimpleCurrentSessionContext(
                Map.of(Thread.currentThread(), new EntityManagerImpl(null, null)));
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(currentSessionContext, null, null);
        assertThatThrownBy(entityManagerFactory::openSession)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 현재 스레드에 생성된 EntityManager가 있습니다.");
    }

    @Test
    void 새로운_EntityManager_세션을_생성한다() {
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(
                new SimpleCurrentSessionContext(),
                BasicMetaModel.createPackageMetaModel("hibernate.entity"),
                null
        );
        EntityManager actual = entityManagerFactory.openSession();
        assertThat(actual).isNotNull();
    }

    @Test
    void 현재_열린_EntityManager_세션이_없는데_반환하려하면_예외가_발생한다() {
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(new SimpleCurrentSessionContext(), null, null);
        assertThatThrownBy(entityManagerFactory::currentSession)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("현재 스레드에 생성된 EntityManager가 없습니다.");
    }

    @Test
    void 현재_열린_EntityManager_세션을_반환한다() {
        CurrentSessionContext currentSessionContext = new SimpleCurrentSessionContext(
                Map.of(Thread.currentThread(), new EntityManagerImpl(null, null)));
        EntityManagerFactory entityManagerFactory = new EntityManagerFactoryImpl(currentSessionContext, null, null);

        EntityManager actual = entityManagerFactory.currentSession();
        assertThat(actual).isNotNull();
    }
}
