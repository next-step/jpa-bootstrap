package persistence.entity.loader;

import domain.FixtureEntity;
import mock.MockDmlGenerator;
import mock.MockJdbcTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Application;
import persistence.core.EntityMetadataProvider;
import persistence.core.EntityScanner;

import static org.assertj.core.api.Assertions.assertThat;

class EntityLoadersTest {
    private static EntityMetadataProvider entityMetadataProvider;
    private static EntityScanner entityScanner;
    private Class<FixtureEntity.WithId> fixtureClass;
    private EntityLoaders entityLoaders;

    @BeforeAll
    static void beforeAll() {
        entityScanner = new EntityScanner(Application.class);
        entityMetadataProvider = EntityMetadataProvider.from(entityScanner);
    }

    @BeforeEach
    void setUp() {
        final EntityScanner entityScanner = new EntityScanner(Application.class);
        entityLoaders = new EntityLoaders(entityMetadataProvider, entityScanner, new MockDmlGenerator(), new MockJdbcTemplate());
    }

    @Test
    @DisplayName("EntityLoaders 를 통해 해당 클래스의 EntityLoader 를 사용할 수 있다..")
    void entityLoadersTest() {
        fixtureClass = FixtureEntity.WithId.class;
        final EntityLoader<FixtureEntity.WithId> entityLoader = entityLoaders.getEntityLoader(fixtureClass);
        assertThat(entityLoader).isNotNull();
    }

    @Test
    @DisplayName("EntityLoaders 를 통해 조회된 같은 타입의 EntityLoader 는 같은 객체이다.")
    void entityLoaderCacheTest() {
        fixtureClass = FixtureEntity.WithId.class;
        final EntityLoader<FixtureEntity.WithId> entityLoader = entityLoaders.getEntityLoader(fixtureClass);
        final EntityLoader<FixtureEntity.WithId> entityLoaderV2 = entityLoaders.getEntityLoader(fixtureClass);
        assertThat(entityLoader == entityLoaderV2).isTrue();
    }

}
