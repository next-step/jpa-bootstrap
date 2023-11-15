package persistence.entity.persister;

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

class EntityPersistersTest {
    private static EntityMetadataProvider entityMetadataProvider;
    private static EntityScanner entityScanner;

    private Class<?> fixtureClass;
    private EntityPersisters entityPersisters;

    @BeforeAll
    static void beforeAll() {
        entityScanner = new EntityScanner(Application.class);
        entityMetadataProvider = EntityMetadataProvider.from(entityScanner);
    }

    @BeforeEach
    void setUp() {
        entityPersisters = new EntityPersisters(entityMetadataProvider, entityScanner, new MockDmlGenerator(), new MockJdbcTemplate());
    }

    @Test
    @DisplayName("entityPersisters 를 통해 해당 클래스의 EntityPersister 를 사용할 수 있다..")
    void entityPersistersTest() {
        fixtureClass = FixtureEntity.WithId.class;
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(fixtureClass);
        assertThat(entityPersister).isNotNull();
    }

    @Test
    @DisplayName("entityPersisters 를 통해 조회된 같은 타입의 EntityPersister 는 같은 객체이다.")
    void entityPersisterCacheTest() {
        fixtureClass = FixtureEntity.WithId.class;
        final EntityPersister entityPersister = entityPersisters.getEntityPersister(fixtureClass);
        final EntityPersister entityPersisterV2 = entityPersisters.getEntityPersister(fixtureClass);
        assertThat(entityPersister == entityPersisterV2).isTrue();
    }

}
