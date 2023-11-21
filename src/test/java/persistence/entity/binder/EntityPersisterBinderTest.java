package persistence.entity.binder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.OneToManyEntityPersister;
import persistence.entity.persister.OneToManyLazyEntityPersister;
import persistence.entity.persister.SimpleEntityPersister;
import persistence.fake.FakeDialect;
import persistence.fake.MockJdbcTemplate;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.assosiate.LazyLoadOrder;
import persistence.testFixtures.assosiate.NoOneToManyOrder;
import persistence.testFixtures.assosiate.Order;

@DisplayName("EntityPersisterBinder 테스트")
class EntityPersisterBinderTest {

    @ParameterizedTest
    @MethodSource("펙토리에_맞는_Persiter")
    @DisplayName("EntityPersister를 상황에 맞게 생성된다.")
    void create(Class<?> entityType, Class<?> resultType) {
        //given
        EntityMeta entityMeta = EntityMeta.from(entityType);
        final QueryGenerator queryGenerator = QueryGenerator.of(entityType, new FakeDialect());

        //when
        EntityPersister entityPersister =  EntityPersisterBinder.bind(new MockJdbcTemplate(), queryGenerator, entityMeta);

        //then
        assertThat(entityPersister).isInstanceOf(resultType);
    }

    private static Stream<Arguments> 펙토리에_맞는_Persiter() {
        return Stream.of(
                Arguments.of(Order.class, OneToManyEntityPersister.class),
                Arguments.of(NoOneToManyOrder.class, SimpleEntityPersister.class),
                Arguments.of(LazyLoadOrder.class, OneToManyLazyEntityPersister.class)
        );
    }

}
