package persistence.sql.dml;

import domain.Order;
import domain.OrderItem;
import fixture.EntityWithId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityTable;

import static org.assertj.core.api.Assertions.*;

class InsertQueryTest {
    @Test
    @DisplayName("insert 쿼리를 생성한다.")
    void insert() {
        // given
        final InsertQuery insertQuery = InsertQuery.getInstance();
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);
        final EntityTable entityTable = new EntityTable(EntityWithId.class);

        // when
        final String sql = insertQuery.insert(entityTable, entity);

        // then
        assertThat(sql).isEqualTo("INSERT INTO entity (nick_name, old, email) VALUES ('Jaden', 30, 'test@email.com')");
    }

    @Test
    @DisplayName("연관관계가 존재하는 부모 엔티티로 insert 쿼리를 생성한다.")
    void insert_withAssociationParent() {
        // given
        final InsertQuery insertQuery = InsertQuery.getInstance();
        final Order order = new Order("OrderNumber1");
        final EntityTable entityTable = new EntityTable(Order.class);

        // when
        final String sql = insertQuery.insert(entityTable, order);

        // then
        assertThat(sql).isEqualTo("INSERT INTO orders (orderNumber) VALUES ('OrderNumber1')");
    }

    @Test
    @DisplayName("연관관계가 존재하는 자식 엔티티로 insert 쿼리를 생성한다.")
    void insert_withAssociationChild() {
        // given
        final InsertQuery insertQuery = InsertQuery.getInstance();
        final Order order = new Order(1L, "OrderNumber1");
        final OrderItem orderItem = new OrderItem("Product1", 10);
        order.addOrderItem(orderItem);

        final EntityTable parentEntityTable = new EntityTable(Order.class);
        final EntityTable entityTable = new EntityTable(OrderItem.class);

        // when
        final String sql = insertQuery.insert(
                entityTable, parentEntityTable.getAssociationColumnName(), parentEntityTable.getIdValue(order), orderItem);

        // then
        assertThat(sql).isEqualTo("INSERT INTO order_items (product, quantity, order_id) VALUES ('Product1', 10, 1)");
    }
}
