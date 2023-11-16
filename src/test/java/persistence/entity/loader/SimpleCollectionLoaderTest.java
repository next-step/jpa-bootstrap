package persistence.entity.loader;

import entity.Order;
import entity.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.entity.attribute.EntityAttributes;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.SimpleEntityPersister;
import persistence.sql.infra.H2SqlConverter;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("SimpleCollectionLoader 클래스의")
class SimpleCollectionLoaderTest extends DatabaseTest {
    EntityAttributes entityAttributes = new EntityAttributes();

    @Nested
    @DisplayName("loadCollection 메소드는")
    class loadCollection {

        @Nested
        @DisplayName("클래스 정보와 쿼리하려는 기준 칼럼과 값이 주어지면")
        public class withValidArgs {

            @Test
            @DisplayName("적절한 객체 리스트를 반환한다.")
            void returnListObject() {
                setUpFixtureTable(Order.class, new H2SqlConverter());
                setUpFixtureTable(OrderItem.class, new H2SqlConverter());

                EntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate);
                CollectionLoader collectionLoader = new SimpleCollectionLoader(jdbcTemplate);
                EntityPersister entityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader, entityAttributes);
                OrderItem orderItemOne = new OrderItem("티비", 1, 1L);
                OrderItem orderItemTwo = new OrderItem("세탁기", 3, 1L);
                OrderItem insertedOrderItemOne = entityPersister.insert(orderItemOne);
                OrderItem insertedOrderItemTwo = entityPersister.insert(orderItemTwo);
                Order order = new Order("1324", List.of(insertedOrderItemOne, insertedOrderItemTwo));
                entityPersister.insert(order);

                List<OrderItem> orderItems = collectionLoader.loadCollection(
                        entityAttributes.findEntityAttribute(OrderItem.class), "order_id", "1");

                assertThat(orderItems.toString()).isEqualTo(
                        "[OrderItem{id=1, product='티비', quantity=1, orderId=1}, OrderItem{id=2, product='세탁기', quantity=3, orderId=1}]");
            }
        }
    }
}
