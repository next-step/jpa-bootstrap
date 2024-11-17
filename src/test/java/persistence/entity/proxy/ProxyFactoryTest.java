package persistence.entity.proxy;

import database.H2ConnectionFactory;
import domain.OrderItem;
import domain.OrderLazy;
import jdbc.JdbcTemplate;
import jdbc.mapper.DefaultRowMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.Metadata;
import persistence.entity.loader.CollectionLoader;
import persistence.entity.manager.EntityManager;
import persistence.meta.EntityTable;
import util.TestHelper;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ProxyFactoryTest {
    private Metadata metadata;
    private final OrderLazy order = new OrderLazy("OrderNumber1");
    private final OrderItem orderItem1 = new OrderItem("Product1", 10);
    private final OrderItem orderItem2 = new OrderItem("Product2", 20);

    @BeforeEach
    void setUp() {
        metadata = TestHelper.createMetadata("domain", "fixture");
        final EntityManager entityManager = metadata.getEntityManagerFactory().openSession();
        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);
        entityManager.persist(order);
    }

    @AfterEach
    void tearDown() {
        metadata.close();
    }

    @Test
    @DisplayName("프록시 생성 후 컬렉션에 접근하면 lazy 로딩 된다.")
    void createProxyAndLazyLoading() {
        // given
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());
        final ProxyFactory proxyFactory = ProxyFactory.getInstance();
        final EntityTable entityTable = new EntityTable(OrderLazy.class);
        final EntityTable childEntityTable = new EntityTable(entityTable.getAssociationColumnType());
        final DefaultRowMapper rowMapper = new DefaultRowMapper(childEntityTable);
        final CollectionLoader collectionLoader = new CollectionLoader(childEntityTable, jdbcTemplate, rowMapper);
        final LazyLoader lazyLoader = new LazyLoader(entityTable, collectionLoader);

        // when
        final List<OrderItem> proxy = (List<OrderItem>) proxyFactory.createProxy(order, lazyLoader);

        // then
        assertAll(
                () -> assertThat(proxy).hasSize(2),
                () -> assertThat(proxy).containsExactly(orderItem1, orderItem2)
        );
    }
}
