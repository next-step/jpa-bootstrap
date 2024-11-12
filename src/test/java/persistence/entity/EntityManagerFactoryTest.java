package persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.Metamodel;
import persistence.entity.manager.CurrentSessionContext;
import persistence.entity.manager.EntityManager;
import persistence.entity.manager.factory.EntityManagerFactory;
import util.TestHelper;

import static org.assertj.core.api.Assertions.*;

class EntityManagerFactoryTest {
    private CurrentSessionContext currentSessionContext;
    private Metamodel metamodel;

    @BeforeEach
    void setUp() {
        currentSessionContext = new CurrentSessionContext();
        metamodel = TestHelper.createMetamodel("domain");
    }

    @Test
    @DisplayName("신규 세션을 오픈한다.")
    void openSession() {
        // given
        final EntityManagerFactory entityManagerFactory = new EntityManagerFactory(currentSessionContext, metamodel);

        // when
        final EntityManager entityManager = entityManagerFactory.openSession();

        // then
        assertThat(entityManager).isNotNull();
    }

    @Test
    @DisplayName("세션 오픈 후 재오픈하면 동일한 세션을 반환한다.")
    void openSession_repeat() {
        // given
        final EntityManagerFactory entityManagerFactory = new EntityManagerFactory(currentSessionContext, metamodel);
        final EntityManager entityManager = entityManagerFactory.openSession();

        // when
        final EntityManager newEntityManager = entityManagerFactory.openSession();

        // then
        assertThat(newEntityManager).isSameAs(entityManager);
    }

    @Test
    @DisplayName("세션을 종료한다.")
    void closeSession() {
        // given
        final EntityManagerFactory entityManagerFactory = new EntityManagerFactory(currentSessionContext, metamodel);
        final EntityManager entityManager = entityManagerFactory.openSession();

        // when
        entityManagerFactory.closeSession();

        // then
        final EntityManager newEntityManager = entityManagerFactory.openSession();
        assertThat(newEntityManager).isNotSameAs(entityManager);
    }
}
