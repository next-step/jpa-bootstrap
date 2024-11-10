package persistence.sql.dml.query;

import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityPersister;
import persistence.fixtures.TestLazyOrder;
import persistence.fixtures.TestLazyOrderItem;
import persistence.meta.Metadata;
import persistence.meta.MetadataImpl;
import persistence.meta.Metamodel;
import persistence.sql.definition.TableDefinition;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class SelectQueryBuilderTest {

    private final Metadata metadata = new MetadataImpl(new H2());
    private final Metamodel metamodel = new Metamodel(metadata, new JdbcTemplate(metadata.getDatabase().getConnection()));

    SelectQueryBuilderTest() throws SQLException {
    }

    @Test
    void testSelectSingleTable() {
        TestLazyOrder order = new TestLazyOrder("order_number");
        String selectQuery = new SelectQueryBuilder(order.getClass(), metamodel).buildById(1);
        assertThat(selectQuery).isEqualTo(
                "SELECT " +
                        "lazy_orders.order_id AS lazy_orders_order_id, " +
                        "lazy_orders.orderNumber AS lazy_orders_orderNumber " +
                        "FROM lazy_orders " +
                        "WHERE lazy_orders.order_id = 1;");
    }

    @Test
    void testSelectSingleTableWithJoin() {
        TestLazyOrder order = new TestLazyOrder("order_number");
        EntityPersister entityPersister = metamodel.findEntityPersister(order.getClass());

        SelectQueryBuilder selectQuery = new SelectQueryBuilder(order.getClass(), metamodel);
        entityPersister.getAssociations().forEach(association -> {
            selectQuery.join(metamodel.findEntityPersister(association.getAssociatedEntityClass()));
        });

        assertThat(selectQuery.buildById(1)).isEqualTo(
                "SELECT lazy_orders.order_id AS lazy_orders_order_id, " +
                        "lazy_orders.orderNumber AS lazy_orders_orderNumber, " +
                        "lazy_order_items.id AS lazy_order_items_id, " +
                        "lazy_order_items.product AS lazy_order_items_product, " +
                        "lazy_order_items.quantity AS lazy_order_items_quantity " +
                        "FROM lazy_orders " +
                        "LEFT JOIN lazy_order_items " +
                        "ON lazy_order_items.order_id = lazy_orders.order_id " +
                        "WHERE lazy_orders.order_id = 1;");
    }

    @Test
    void testFindAll() {
        TestLazyOrder order = new TestLazyOrder(1L, "order_number");
        EntityPersister entityPersister = metamodel.findEntityPersister(order.getClass());
        String joinColumnName = entityPersister.getJoinColumnName(TestLazyOrderItem.class);
        Object joinColumnValue = entityPersister.getValue(order, joinColumnName);

        TestLazyOrderItem orderItem = new TestLazyOrderItem("product", 1);
        String selectQuery = new SelectQueryBuilder(orderItem.getClass(), metamodel)
                .where(joinColumnName, joinColumnValue.toString())
                .build();

        assertThat(selectQuery).isEqualTo(
                "SELECT lazy_order_items.id AS lazy_order_items_id, " +
                        "lazy_order_items.product AS lazy_order_items_product, " +
                        "lazy_order_items.quantity AS lazy_order_items_quantity " +
                        "FROM lazy_order_items " +
                        "WHERE lazy_order_items.order_id = '1'");
    }
}
