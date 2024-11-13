package persistence.entity;

import domain.Order;
import domain.OrderItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.Metamodel;
import persistence.entity.manager.CurrentSessionContext;
import persistence.entity.manager.EntityManager;
import persistence.entity.manager.factory.EntityManagerFactory;
import util.TestHelper;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class EntityManagerFactoryTest {
    private CurrentSessionContext currentSessionContext;
    private Metamodel metamodel;

    @BeforeEach
    void setUp() {
        currentSessionContext = new CurrentSessionContext();
        metamodel = TestHelper.createMetamodel("domain");
    }

    @AfterEach
    void tearDown() {
        metamodel.close();
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

    @Test
    @DisplayName("신규 세션을 오픈하고 엔티티를 영속화한다.")
    void openSessionAndPersist() {
        // given
        final EntityManagerFactory entityManagerFactory = new EntityManagerFactory(currentSessionContext, metamodel);
        final EntityManager entityManager = entityManagerFactory.openSession();

        final Order order = new Order("orderNumber");
        final OrderItem orderItem1 = new OrderItem("product1", 10);
        final OrderItem orderItem2 = new OrderItem("product2", 20);
        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);

        // when
        entityManager.persist(order);

        // then
        final Order managedOrder = entityManager.find(Order.class, order.getId());
        assertAll(
                () -> assertThat(managedOrder.getId()).isNotNull(),
                () -> assertThat(managedOrder.getOrderNumber()).isEqualTo(order.getOrderNumber()),
                () -> assertThat(managedOrder.getOrderItems()).hasSize(2)
        );
    }
}
