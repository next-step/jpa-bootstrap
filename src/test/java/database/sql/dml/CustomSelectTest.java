package database.sql.dml;

import app.entity.EagerLoadTestOrder;
import database.sql.dml.part.WhereMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.Metadata;

import static org.assertj.core.api.Assertions.assertThat;
import static testsupport.EntityTestUtils.initializer;

class CustomSelectTest {
    private CustomSelect customSelect;

    @BeforeEach
    void setUp() {
        Metadata metadata = initializer(null).getMetadata();
        customSelect = CustomSelect.from(metadata.getPersistentClass(EagerLoadTestOrder.class), metadata);
    }

    @Test
    void buildSelectQueryWithoutCondition() {
        String actual = customSelect.toSql();
        assertThat(actual).isEqualTo("SELECT t.id, t.orderNumber, a0.order_id, a0.id, a0.product, a0.quantity FROM eagerload_orders t LEFT JOIN eagerload_order_items a0 ON t.id = a0.order_id");
    }

    @Test
    void buildSelectQueryWithId() {
        String actual = customSelect.toSql(WhereMap.of("id", "123"));
        assertThat(actual).isEqualTo("SELECT t.id, t.orderNumber, a0.order_id, a0.id, a0.product, a0.quantity FROM eagerload_orders t LEFT JOIN eagerload_order_items a0 ON t.id = a0.order_id WHERE t.id = 123");
    }

    @Test
    void buildSelectQueryWithCondition() {
        String actual = customSelect.toSql(WhereMap.of("orderNumber", "order-1"));
        assertThat(actual).isEqualTo("SELECT t.id, t.orderNumber, a0.order_id, a0.id, a0.product, a0.quantity FROM eagerload_orders t LEFT JOIN eagerload_order_items a0 ON t.id = a0.order_id WHERE t.orderNumber = 'order-1'");
    }
}
