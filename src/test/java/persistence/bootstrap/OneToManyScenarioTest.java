package persistence.bootstrap;

import app.entity.EagerLoadTestOrder;
import app.entity.EagerLoadTestOrderItem;
import app.entity.LazyLoadTestOrder;
import app.entity.LazyLoadTestOrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.database.EntityPersister;
import persistence.entitymanager.EntityManager;
import persistence.entitymanager.SessionContract;
import testsupport.H2DatabaseTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class OneToManyScenarioTest extends H2DatabaseTest {
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        Initializer initializer = new Initializer("app.entity", jdbcTemplate, dialect);
        initializer.initialize();
        EntityManagerFactory entityManagerFactory = initializer.createEntityManagerFactory();

        entityManager = entityManagerFactory.openSession();

        List<Class<?>> classes = List.of(EagerLoadTestOrder.class, EagerLoadTestOrderItem.class, LazyLoadTestOrder.class, LazyLoadTestOrderItem.class);
        for (Class<?> clazz : classes) {
            EntityPersister<?> entityPersister = ((SessionContract) entityManager).getEntityPersister(clazz);
            entityPersister.dropTable(true);
            entityPersister.createTable();
        }

        jdbcTemplate.execute("INSERT INTO eagerload_orders (orderNumber) VALUES (1234)");
        jdbcTemplate.execute("INSERT INTO eagerload_order_items (product, quantity, order_id) VALUES ('product1', 5, 1)");
        jdbcTemplate.execute("INSERT INTO eagerload_order_items (product, quantity, order_id) VALUES ('product20', 50, 1)");

        jdbcTemplate.execute("INSERT INTO lazyload_orders (orderNumber) VALUES (1234)");
        jdbcTemplate.execute("INSERT INTO lazyload_order_items (product, quantity, order_id) VALUES ('product1', 5, 1)");
        jdbcTemplate.execute("INSERT INTO lazyload_order_items (product, quantity, order_id) VALUES ('product20', 50, 1)");

        executedQueries.clear();
    }

    @Test
    @DisplayName("FetchType.EAGER 연관관계를 가진 객체를 가져오기")
    void scenario6() {
        EagerLoadTestOrder order = entityManager.find(EagerLoadTestOrder.class, 1L);

        assertAll(
                () -> assertThat(order)
                        .hasFieldOrPropertyWithValue("id", 1L)
                        .hasFieldOrPropertyWithValue("orderNumber", "1234"),
                () -> assertThat(order.getOrderItems()).hasSize(2),
                () -> assertThat(order.getOrderItems().get(0))
                        .hasFieldOrPropertyWithValue("id", 1L)
                        .hasFieldOrPropertyWithValue("product", "product1")
                        .hasFieldOrPropertyWithValue("quantity", 5),
                () -> assertThat(order.getOrderItems().get(1))
                        .hasFieldOrPropertyWithValue("id", 2L)
                        .hasFieldOrPropertyWithValue("product", "product20")
                        .hasFieldOrPropertyWithValue("quantity", 50),
                () -> assertThat(executedQueries).isEqualTo(List.of(
                        "SELECT t.id, t.orderNumber, a0.order_id, a0.id, a0.product, a0.quantity FROM eagerload_orders t LEFT JOIN eagerload_order_items a0 ON t.id = a0.order_id WHERE t.id = 1"))
        );
    }

    @Test
    @DisplayName("FetchType.LAZY 연관관계를 가진 객체를 가져오기")
    void scenario7() {
        LazyLoadTestOrder lazyLoadTestOrder = entityManager.find(LazyLoadTestOrder.class, 1L);
        List<LazyLoadTestOrderItem> orderItems = lazyLoadTestOrder.getOrderItems();
        assertThat(orderItems.size()).isEqualTo(2);

        assertAll(
                () -> assertThat(orderItems.size()).isEqualTo(2),
                () -> assertThat(orderItems.get(0))
                        .hasFieldOrPropertyWithValue("id", 1L)
                        .hasFieldOrPropertyWithValue("product", "product1")
                        .hasFieldOrPropertyWithValue("quantity", 5),
                () -> assertThat(orderItems.get(1))
                        .hasFieldOrPropertyWithValue("id", 2L)
                        .hasFieldOrPropertyWithValue("product", "product20")
                        .hasFieldOrPropertyWithValue("quantity", 50),
                () -> assertThat(executedQueries).isEqualTo(List.of(
                        "SELECT t.id, t.orderNumber FROM lazyload_orders t WHERE t.id = 1",
                        "SELECT id, product, quantity FROM lazyload_order_items WHERE order_id = 1"
                ))
        );
    }
}
