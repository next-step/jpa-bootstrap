package persistence.sql.ddl;

import domain.Order;
import domain.OrderItem;
import fixture.EntityWithId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.dialect.H2Dialect;
import persistence.meta.EntityTable;

import static org.assertj.core.api.Assertions.*;

class CreateQueryTest {
    @Test
    @DisplayName("create 쿼리를 생성한다.")
    void create() {
        // given
        final Dialect dialect = new H2Dialect();
        final EntityTable entityTable = new EntityTable(EntityWithId.class);
        final CreateQuery createQuery = new CreateQuery(entityTable, dialect);

        // when
        final String sql = createQuery.create();

        // then
        assertThat(sql).isEqualTo(
                "CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, nick_name VARCHAR(20), old INTEGER, email VARCHAR(255) NOT NULL)");
    }

    @Test
    @DisplayName("연관관계가 존재하는 엔티티로 create 쿼리를 생성한다.")
    void create_withAssociation() {
        // given
        final Dialect dialect = new H2Dialect();
        final EntityTable parentEntityTable = new EntityTable(Order.class);
        final EntityTable entityTable = new EntityTable(OrderItem.class);
        final CreateQuery createQuery = new CreateQuery(entityTable, dialect);

        // when
        final String sql = createQuery.create(parentEntityTable);

        // then
        assertThat(sql).isEqualTo(
                "CREATE TABLE order_items (id BIGINT AUTO_INCREMENT PRIMARY KEY, product VARCHAR(255), quantity INTEGER, order_id BIGINT)");
    }
}
