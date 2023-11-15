package persistence.entity.context;

import domain.FixtureEntity;
import domain.FixtureEntity.Person;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Application;
import persistence.context.EntityKey;
import persistence.context.EntityKeyGenerator;
import persistence.core.EntityMetadataProvider;
import persistence.core.EntityScanner;

import static org.assertj.core.api.Assertions.assertThat;

class EntityKeyGeneratorTest {
    private static EntityMetadataProvider entityMetadataProvider;
    private EntityKeyGenerator entityKeyGenerator;

    @BeforeAll
    static void beforeAll() {
        entityMetadataProvider = EntityMetadataProvider.from(new EntityScanner(Application.class));
    }

    @BeforeEach
    void setUp() {
        entityKeyGenerator = new EntityKeyGenerator(entityMetadataProvider);
    }

    @Test
    @DisplayName("같은 클래스의 같은 Id 로 key 생성시 동일한 key 객체를 생성한다.")
    void entityKeyGeneratorTest() {
        final EntityKey entityKey1 = entityKeyGenerator.generate(Person.class, 1L);
        final EntityKey entityKey2 = entityKeyGenerator.generate(Person.class, 1L);

        assertThat(entityKey1 == entityKey2).isTrue();
    }

    @Test
    @DisplayName("같은 클래스의 다른 Id 로 key 생성시 동일하지 않은 key 객체를 생성한다.")
    void entityKeyGeneratorDifferentIdTest() {
        final EntityKey entityKey1 = entityKeyGenerator.generate(Person.class, 1L);
        final EntityKey entityKey2 = entityKeyGenerator.generate(Person.class, 2L);

        assertThat(entityKey1 == entityKey2).isFalse();
    }

    @Test
    @DisplayName("다른 클래스의 같은 Id 로 key 생성시 동일하지 않은 key 객체를 생성한다.")
    void entityKeyGeneratorDifferentEntityTest() {
        final EntityKey entityKey1 = entityKeyGenerator.generate(Person.class, 1L);
        final EntityKey entityKey2 = entityKeyGenerator.generate(FixtureEntity.WithId.class, 1L);

        assertThat(entityKey1 == entityKey2).isFalse();
    }

    @Test
    @DisplayName("다른 클래스의 다른 Id 로 key 생성시 동일하지 않은 key 객체를 생성한다.")
    void entityKeyGeneratorDifferentIdAndEntityTest() {
        final EntityKey entityKey1 = entityKeyGenerator.generate(Person.class, 1L);
        final EntityKey entityKey2 = entityKeyGenerator.generate(FixtureEntity.WithId.class, 2L);

        assertThat(entityKey1 == entityKey2).isFalse();
    }
}
