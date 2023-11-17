package persistence.entity.manager;

import mock.MockPersistenceEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Application;
import persistence.core.EntityScanner;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleEntityManagerFactoryTest {

    @Test
    @DisplayName("EntityManagerFactory 를 이용해 EntityManager 를 생성할 수 있다.")
    void entityManagerFactoryOpenSessionTest() {
        final EntityScanner entityScanner = new EntityScanner(Application.class);
        final EntityManagerFactory entityManagerFactory = new SimpleEntityManagerFactory(entityScanner, new MockPersistenceEnvironment());

        final EntityManager entityManager = entityManagerFactory.openSession();

        assertThat(entityManager).isNotNull();
    }

    @Test
    @DisplayName("EntityManagerFactory.openSession 을 두번이상 호출하면 동일한 EntityManager 를 반환한다.")
    void entityManagerFactoryAlreadySessionOpenTest() {
        final EntityScanner entityScanner = new EntityScanner(Application.class);
        final EntityManagerFactory entityManagerFactory = new SimpleEntityManagerFactory(entityScanner, new MockPersistenceEnvironment());

        final EntityManager entityManagerV1 = entityManagerFactory.openSession();
        final EntityManager entityManagerV2 = entityManagerFactory.openSession();

        assertThat(entityManagerV1 == entityManagerV2).isTrue();
    }
}
