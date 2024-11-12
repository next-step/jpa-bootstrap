package study.lazyloading;

import boot.MetaModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.TestEntityInitialize;
import persistence.config.TestPersistenceConfig;
import persistence.proxy.ProxyFactory;
import persistence.proxy.impl.LazyLoadingHandler;
import persistence.sql.context.CollectionKeyHolder;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.Database;
import persistence.sql.dml.impl.SimpleMetadataLoader;
import persistence.sql.entity.CollectionEntry;
import persistence.sql.entity.data.Status;
import persistence.sql.fixture.LazyTestOrder;
import persistence.sql.fixture.LazyTestOrderItem;
import persistence.sql.fixture.TestOrder;
import persistence.sql.fixture.TestOrderItem;
import persistence.sql.loader.EntityLoader;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("LazyLoadingHandler 테스트")
class LazyLoadingHandlerTest extends TestEntityInitialize {
    private final ProxyFactory proxyFactory = new TestProxyFactory();
    private PersistenceContext persistenceContext;
    private MetaModel metaModel;

    @BeforeEach
    void setup() throws SQLException {
        TestPersistenceConfig config = TestPersistenceConfig.getInstance();
        Database database = config.database();
        persistenceContext = config.persistenceContext();
        metaModel = config.metaModel();

        database.executeUpdate("INSERT INTO lazy_orders (order_number) VALUES ('1')");
        database.executeUpdate("INSERT INTO lazy_order_items (product, quantity, order_id) VALUES ('apple', 10, 1)");
        database.executeUpdate("INSERT INTO lazy_order_items (product, quantity, order_id) VALUES ('cherry', 20, 1)");
    }

    @Test
    @DisplayName("생성자를 통해 프록시 객체를 생성할 수 있다.")
    void constructor() {
        EntityLoader<TestOrder> loader = metaModel.entityLoader(TestOrder.class);
        EntityLoader<TestOrderItem> targetLoader = metaModel.entityLoader(TestOrderItem.class);
        LazyLoadingHandler<?> handler = LazyLoadingHandler.newInstance(1L, TestOrder.class, persistenceContext, loader, targetLoader);
        CollectionEntry collectionEntry = CollectionEntry.create(new SimpleMetadataLoader<>(TestOrderItem.class), Status.MANAGED, (Collection) handler);
        CollectionKeyHolder collectionKeyHolder = new CollectionKeyHolder(TestOrder.class, 1L, TestOrderItem.class);
        persistenceContext.addCollectionEntry(collectionKeyHolder, collectionEntry);

        Collection<TestOrderItem> proxy = proxyFactory.createProxyCollection(1L, TestOrder.class, TestOrderItem.class, List.class, persistenceContext, loader, targetLoader);

        assertAll(
                () -> assertThat(proxy).isNotNull(),
                () -> assertThat(proxy).hasSize(0)
        );
    }

    @Test
    @DisplayName("객체 필드에 접근시 지연로딩을 수행하며 유효한 값을 반환한다.")
    void invoke() {
        EntityLoader<LazyTestOrder> loader = metaModel.entityLoader(LazyTestOrder.class);
        EntityLoader<LazyTestOrderItem> targetLoader = metaModel.entityLoader(LazyTestOrderItem.class);
        LazyLoadingHandler<?> handler = LazyLoadingHandler.newInstance(1L, LazyTestOrder.class, persistenceContext, loader, targetLoader);
        CollectionEntry collectionEntry = CollectionEntry.create(new SimpleMetadataLoader<>(LazyTestOrderItem.class), Status.MANAGED, (Collection) handler);
        CollectionKeyHolder collectionKeyHolder = new CollectionKeyHolder(LazyTestOrder.class, 1L, LazyTestOrderItem.class);
        persistenceContext.addCollectionEntry(collectionKeyHolder, collectionEntry);

        Collection<LazyTestOrderItem> proxy = proxyFactory.createProxyCollection(1L, LazyTestOrder.class, LazyTestOrderItem.class, List.class, persistenceContext, targetLoader, loader);
        proxy.iterator();

        assertAll(
                () -> assertThat(proxy).isNotNull(),
                () -> assertThat(proxy).hasSize(2),
                () -> assertThat(proxy).containsExactlyInAnyOrder(
                        new LazyTestOrderItem(1L, "apple", 10),
                        new LazyTestOrderItem(2L, "cherry", 20)
                )
        );
    }

    @Test
    @DisplayName("객체 필드에 접근시 영속성 컨텍스트에 관리되지 않는 경우 예외를 던진다.")
    void invokeWithInvalidPersistenceContext() {
        List<Object> proxy = proxyFactory.createProxyCollection(999L, TestOrder.class, TestOrderItem.class, List.class, persistenceContext, metaModel.entityLoader(TestOrderItem.class), metaModel.entityLoader(TestOrder.class));

        assertThatThrownBy(proxy::iterator)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("failed to lazily initialize a collection");
    }
}
