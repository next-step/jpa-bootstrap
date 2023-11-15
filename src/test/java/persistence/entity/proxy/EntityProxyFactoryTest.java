package persistence.entity.proxy;

import domain.FixtureAssociatedEntity.LazyCity;
import domain.FixtureAssociatedEntity.LazyCountry;
import domain.FixtureAssociatedEntity.OrderLazy;
import domain.FixtureAssociatedEntity.OrderLazyItem;
import mock.MockDmlGenerator;
import mock.MockJdbcTemplate;
import net.sf.cglib.proxy.Enhancer;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Application;
import persistence.core.EntityManyToOneColumn;
import persistence.core.EntityMetadataProvider;
import persistence.core.EntityOneToManyColumn;
import persistence.core.EntityScanner;
import persistence.entity.loader.EntityLoaders;
import persistence.util.ReflectionUtils;

import java.sql.Types;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;


class EntityProxyFactoryTest {

    static class MockEntityLoaders extends EntityLoaders {

        public MockEntityLoaders(final SimpleResultSet resultSet) {
            super(EntityMetadataProvider.from(new EntityScanner(Application.class)), new EntityScanner(Application.class), new MockDmlGenerator(), new MockJdbcTemplate(resultSet));
        }
    }

    @Test
    @DisplayName("EntityOneToManyColumn(Lazy) 는 proxy 처리 되어 있으며 객체 메서드 호출시 값을 load 한다.")
    void shouldLoadLazyOneToManyCollection() throws NoSuchFieldException {
        final EntityProxyFactory entityProxyFactory = new EntityProxyFactory(new MockEntityLoaders(createOrderItemResultSet()));
        final Class<OrderLazy> ownerClass = OrderLazy.class;
        final EntityOneToManyColumn oneToManyColumn = new EntityOneToManyColumn(ownerClass.getDeclaredField("orderItems"), "lazy_orders");
        final OrderLazy instance = ReflectionUtils.createInstance(ownerClass);
        entityProxyFactory.initOneToManyProxy(1L, instance, oneToManyColumn);


        assertSoftly(softly -> {
            final List<OrderLazyItem> orderItems = instance.getOrderItems();
            softly.assertThat(Enhancer.isEnhanced(orderItems.getClass())).isTrue();
            softly.assertThat(orderItems).extracting(OrderLazyItem::getId)
                    .containsExactly(1L, 2L, 3L, 4L);
            softly.assertThat(orderItems).extracting(OrderLazyItem::getProduct)
                    .containsExactly("testProduct1", "testProduct2", "testProduct3", "testProduct4");
            softly.assertThat(orderItems).extracting(OrderLazyItem::getQuantity)
                    .containsExactly(400, 300, 200, 100);
        });
    }

    @Test
    @DisplayName("EntityManyToOneColumn(Lazy) 는 proxy 처리 되어 있으며 객체 메서드 호출시 값을 load 한다.")
    void shouldLoadLazyManyToOneEntity() throws NoSuchFieldException {
        final EntityProxyFactory entityProxyFactory = new EntityProxyFactory(new MockEntityLoaders(createCountryResultSet()));
        final Class<LazyCity> ownerClass = LazyCity.class;
        final EntityManyToOneColumn manyToOneColumn = new EntityManyToOneColumn(ownerClass.getDeclaredField("country"), "lazy_city");
        final LazyCity owner = prepareOwnerForManyToOne();

        entityProxyFactory.initManyToOneProxy(owner, manyToOneColumn);

        assertSoftly(softly -> {
            final LazyCountry country = owner.getCountry();
            softly.assertThat(Enhancer.isEnhanced(country.getClass())).isTrue();
            softly.assertThat(country.getId()).isEqualTo(4L);
            softly.assertThat(country.getName()).isEqualTo("testCountry");
        });
    }

    private LazyCity prepareOwnerForManyToOne() {
        final Class<LazyCity> ownerClass = LazyCity.class;
        final LazyCity owner = ReflectionUtils.createInstance(ownerClass);
        final LazyCountry manyToOneEntity = ReflectionUtils.createInstance(LazyCountry.class);
        ReflectionUtils.injectField(manyToOneEntity, "id", 4L);
        ReflectionUtils.injectField(owner, "country", manyToOneEntity);
        return owner;
    }


    private static SimpleResultSet createOrderItemResultSet() {
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("id", Types.BIGINT, 10, 0);
        rs.addColumn("product", Types.VARCHAR, 255, 0);
        rs.addColumn("quantity", Types.INTEGER, 10, 0);
        rs.addColumn("lazy_order_id", Types.BIGINT, 255, 0);
        rs.addRow(1L, "testProduct1", 400, 777L);
        rs.addRow(2L, "testProduct2", 300, 777L);
        rs.addRow(3L, "testProduct3", 200, 777L);
        rs.addRow(4L, "testProduct4", 100, 777L);
        return rs;
    }

    private static SimpleResultSet createCountryResultSet() {
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("id", Types.BIGINT, 10, 0);
        rs.addColumn("name", Types.VARCHAR, 255, 0);
        rs.addRow(4L, "testCountry");
        return rs;
    }
}
