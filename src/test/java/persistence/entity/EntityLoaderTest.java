package persistence.entity;

import database.H2ConnectionFactory;
import domain.Order;
import domain.OrderItem;
import fixture.EntityWithId;
import jdbc.DefaultRowMapper;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.proxy.ProxyFactory;
import persistence.meta.EntityTable;
import persistence.sql.dml.SelectQuery;
import util.TestHelper;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static util.QueryUtils.*;

class EntityLoaderTest {
    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;
    private SelectQuery selectQuery;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());
        entityManager = TestHelper.createEntityManager("domain", "fixture");
        selectQuery = new SelectQuery();

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
        final EntityTable entityTable = new EntityTable(EntityWithId.class);
        final DefaultRowMapper rowMapper = new DefaultRowMapper(entityTable);
        final CollectionLoader collectionLoader = new CollectionLoader(entityTable, jdbcTemplate, selectQuery, rowMapper);
        final EntityLoader entityLoader = new EntityLoader(entityTable, EntityTable.EMPTY, jdbcTemplate, new SelectQuery(), new ProxyFactory(), rowMapper, collectionLoader);
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);
        insertData(entity);

        // when
        final EntityWithId managedEntity = entityLoader.load(entity.getId());

        // then
        assertAll(
                () -> assertThat(managedEntity).isNotNull(),
                () -> assertThat(managedEntity.getId()).isEqualTo(entity.getId()),
                () -> assertThat(managedEntity.getName()).isEqualTo(entity.getName()),
                () -> assertThat(managedEntity.getAge()).isEqualTo(entity.getAge()),
                () -> assertThat(managedEntity.getEmail()).isEqualTo(entity.getEmail()),
                () -> assertThat(managedEntity.getIndex()).isNull()
        );
    }

    @Test
    @DisplayName("연관관계가 존재하는 엔티티를 로드한다.")
    void load_withAssociation() {
        // given
        final EntityTable entityTable = new EntityTable(Order.class);
        final EntityTable childEntityTable = new EntityTable(OrderItem.class);
        final DefaultRowMapper rowMapper = new DefaultRowMapper(entityTable, childEntityTable);
        final CollectionLoader collectionLoader = new CollectionLoader(entityTable, jdbcTemplate, selectQuery, rowMapper);
        final EntityLoader entityLoader = new EntityLoader(entityTable, childEntityTable, jdbcTemplate, new SelectQuery(), new ProxyFactory(), rowMapper, collectionLoader);
        final Order order = new Order("OrderNumber1");
        final OrderItem orderItem1 = new OrderItem("Product1", 10);
        final OrderItem orderItem2 = new OrderItem("Product2", 20);
        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);
        insertData(order);

        // when
        final Order managedOrder = entityLoader.load(order.getId());

        // then
        assertThat(managedOrder).isEqualTo(order);
    }

    private void insertData(Object entity) {
        entityManager.persist(entity);
    }
}
