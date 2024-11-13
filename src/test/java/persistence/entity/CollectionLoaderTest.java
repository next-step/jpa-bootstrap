package persistence.entity;

import database.H2ConnectionFactory;
import domain.Order;
import domain.OrderItem;
import jdbc.JdbcTemplate;
import jdbc.mapper.DefaultRowMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.Metamodel;
import persistence.entity.loader.CollectionLoader;
import persistence.entity.manager.DefaultEntityManager;
import persistence.entity.manager.EntityManager;
import persistence.meta.EntityTable;
import persistence.sql.dml.SelectQuery;
import util.TestHelper;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CollectionLoaderTest {
    private JdbcTemplate jdbcTemplate;
    private Metamodel metamodel;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());
        metamodel = TestHelper.createMetamodel("domain", "fixture");
        entityManager = new DefaultEntityManager(metamodel);
    }

    @AfterEach
    void tearDown() {
        metamodel.close();
    }

    @Test
    @DisplayName("엔티티를 로드한다.")
    void load() {
        // given
        final EntityTable entityTable = new EntityTable(OrderItem.class);
        final DefaultRowMapper rowMapper = new DefaultRowMapper(entityTable);
        final CollectionLoader collectionLoader = new CollectionLoader(entityTable, jdbcTemplate, new SelectQuery(), rowMapper);
        final Order order = new Order("OrderNumber1");
        final OrderItem orderItem1 = new OrderItem("Product1", 10);
        final OrderItem orderItem2 = new OrderItem("Product2", 20);
        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);
        insertData(order);
        final EntityTable parentEntityTable = new EntityTable(Order.class);

        // when
        final List<OrderItem> orderItems =
                (List<OrderItem>) collectionLoader.load(parentEntityTable.getAssociationCondition(order));

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
        entityManager.persist(entity);
    }
}
