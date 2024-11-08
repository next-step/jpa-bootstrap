package persistence.entity;

import database.H2ConnectionFactory;
import domain.Order;
import domain.OrderItem;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.dml.InsertQuery;
import persistence.sql.dml.SelectQuery;
import persistence.sql.dml.UpdateQuery;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static util.QueryUtils.*;

class CollectionLoaderTest {
    private JdbcTemplate jdbcTemplate;
    private EntityPersister entityPersister;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());
        entityPersister = new DefaultEntityPersister(jdbcTemplate, new InsertQuery(), new UpdateQuery(), new DeleteQuery());

        createTable(EntityWithId.class);
        createTable(Order.class);
        createTable(OrderItem.class, Order.class);
    }

    @AfterEach
    void tearDown() {
        dropTable(EntityWithId.class);
        dropTable(Order.class);
        dropTable(OrderItem.class);
    }

    @Test
    @DisplayName("엔티티를 로드한다.")
    void load() {
        // given
        final CollectionLoader collectionLoader = new CollectionLoader(jdbcTemplate, new SelectQuery());
        final Order order = new Order("OrderNumber1");
        insertData(order);
        final OrderItem orderItem1 = new OrderItem("Product1", 10);
        order.addOrderItem(orderItem1);
        insertData(orderItem1, order);
        final OrderItem orderItem2 = new OrderItem("Product2", 20);
        order.addOrderItem(orderItem2);
        insertData(orderItem2, order);

        // when
        final List<OrderItem> orderItems = collectionLoader.load(OrderItem.class, "order_id", order.getId());

        // then
        assertAll(
                () -> assertThat(orderItems).hasSize(2),
                () -> assertThat(orderItems).contains(
                        new OrderItem(1L, "Product1", 10),
                        new OrderItem(2L, "Product2", 20)
                )
        );
    }

    private void insertData(Object entity) {
        entityPersister.insert(entity);
    }

    private void insertData(Object entity, Object parentEntity) {
        entityPersister.insert(entity, parentEntity);
    }
}
