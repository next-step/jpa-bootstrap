package persistence.sql.dml.query;

import org.junit.jupiter.api.Test;
import persistence.fixtures.TestLazyOrder;
import persistence.fixtures.TestLazyOrderItem;
import persistence.meta.Metamodel;
import persistence.meta.MetamodelInitializer;
import persistence.sql.definition.TableDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class SelectQueryBuilderTest {

    private final Metamodel metamodel = new MetamodelInitializer(null).getMetamodel();

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
        TableDefinition orderTableDefinition = metamodel.findTableDefinition(order.getClass());

        SelectQueryBuilder selectQuery = new SelectQueryBuilder(order.getClass(), metamodel);
        orderTableDefinition.getAssociations().forEach(association -> {
            selectQuery.join(metamodel.findTableDefinition(association.getAssociatedEntityClass()));
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
        TableDefinition orderTableDefinition = metamodel.findTableDefinition(order.getClass());
        String joinColumnName = orderTableDefinition.getJoinColumnName(TestLazyOrderItem.class);
        Object joinColumnValue = orderTableDefinition.getValue(order, joinColumnName);

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
