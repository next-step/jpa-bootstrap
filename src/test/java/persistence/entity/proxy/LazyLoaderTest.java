package persistence.entity.proxy;

import database.H2ConnectionFactory;
import domain.Order;
import domain.OrderItem;
import domain.OrderLazy;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.CollectionLoader;
import persistence.entity.LazyLoader;
import persistence.sql.dml.SelectQuery;

import static org.assertj.core.api.Assertions.*;

class LazyLoaderTest {
    private JdbcTemplate jdbcTemplate;
    private SelectQuery selectQuery;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());
        selectQuery = new SelectQuery();
    }

    @Test
    @DisplayName("FetchType이 LAZY인 클래스로 인스턴스를 생성한다.")
    void constructor() {
        // given
        final OrderLazy order = new OrderLazy("OrderNumber1");
        final CollectionLoader collectionLoader = new CollectionLoader(jdbcTemplate, selectQuery);

        // when
        final LazyLoader<OrderItem> lazyLoader = new LazyLoader<>(OrderItem.class, order, collectionLoader);

        // then
        assertThat(lazyLoader).isNotNull();
    }

    @Test
    @DisplayName("FetchType이 LAZY가 아닌 클래스로 인스턴스를 생성하면 예외를 발생한다.")
    void constructor_exception() {
        // given
        final Order order = new Order("OrderNumber1");
        final CollectionLoader collectionLoader = new CollectionLoader(jdbcTemplate, selectQuery);

        // when & then
        assertThatThrownBy(() -> new LazyLoader<>(OrderItem.class, order, collectionLoader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(LazyLoader.NO_ONE_TO_ONE_LAZY_FAILED_MESSAGE);
    }
}
