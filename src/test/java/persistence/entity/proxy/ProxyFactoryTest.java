package persistence.entity.proxy;

class ProxyFactoryTest {
//    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());
//    private final Order order = new Order("OrderNumber1");
//    private final OrderItem orderItem1 = new OrderItem("Product1", 10);
//    private final OrderItem orderItem2 = new OrderItem("Product2", 20);

//    @BeforeEach
//    void setUp() {
//        createTable(OrderLazy.class);
//        createTable(OrderItem.class, OrderLazy.class);
//
//        final EntityPersister entityPersister = new DefaultEntityPersister(
//                jdbcTemplate, new InsertQuery(), new UpdateQuery(), new DeleteQuery());
//
//        entityPersister.insert(order);
//        order.addOrderItem(orderItem1);
//        entityPersister.insert(orderItem1, order);
//        order.addOrderItem(orderItem2);
//        entityPersister.insert(orderItem2, order);
//    }
//
//    @AfterEach
//    void tearDown() {
//        dropTable(OrderLazy.class);
//        dropTable(OrderItem.class);
//    }

//    @Test
//    @DisplayName("프록시 생성 후 컬렉션에 접근하면 lazy 로딩 된다.")
//    void createProxyAndLazyLoading() {
//        // given
//        final ProxyFactory proxyFactory = new ProxyFactory();
//        final CollectionLoader collectionLoader = new CollectionLoader(jdbcTemplate, new SelectQuery());
//        final OrderLazy managedOrder = collectionLoader.load(OrderLazy.class, order.getId());
//
//        // when
//        final List<OrderItem> proxy = proxyFactory.createProxy(collectionLoader, OrderItem.class, managedOrder);
//        proxy.size();
//
//        // then
//        assertAll(
//                () -> assertThat(proxy).hasSize(2),
//                () -> assertThat(proxy).containsExactly(orderItem1, orderItem2)
//        );
//    }
}
