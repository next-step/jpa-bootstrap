package persistence.entity.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import persistence.entity.EntityManager;
import registry.EntityMetaRegistry;

@DisplayName("EntityManagerFactory 테스트")
class EntityManagerFactoryTest {

    @Mock
    private Connection connection;

    @Mock
    private EntityMetaRegistry entityMetaRegistry;

    @Test
    @DisplayName("EntityManagerFactory를 통해 EntityManager를 생성할 수 있다.")
    void createEntityManager() {
        final EntityManagerFactory entityManagerFactory = new EntityManagerFactory(connection, entityMetaRegistry);
        final EntityManager entityManager = entityManagerFactory.openSession();

        assertThat(entityManager).isNotNull();
    }

    @Test
    @DisplayName("하나의 EntityManagerFactory에서 열린 EntityManager는 동일하다.")
    void sameEntityManagerFactoryOpenEntityManagerIsIdentical() {
        final EntityManagerFactory entityManagerFactory = new EntityManagerFactory(connection, entityMetaRegistry);
        final EntityManager entityManager1 = entityManagerFactory.openSession();
        final EntityManager entityManager2 = entityManagerFactory.openSession();

        assertThat(entityManager1 == entityManager2).isTrue();
    }
}
