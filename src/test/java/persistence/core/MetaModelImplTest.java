package persistence.core;

import domain.FixtureAssociatedEntity;
import domain.FixtureEntity;
import mock.MockDmlGenerator;
import mock.MockJdbcTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Application;
import persistence.entity.loader.EntityLoaders;
import persistence.entity.persister.EntityPersisters;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MetaModelImplTest {

    private static EntityMetadataProvider entityMetadataProvider;
    private static EntityPersisters entityPersisters;
    private static EntityLoaders entityLoaders;
    private MetaModelImpl metaModel;

    @BeforeAll
    static void beforeAll() {
        entityMetadataProvider = EntityMetadataProvider.from(new EntityScanner(Application.class));
        final MockDmlGenerator dmlGenerator = new MockDmlGenerator();
        final MockJdbcTemplate jdbcTemplate = new MockJdbcTemplate();
        entityPersisters = new EntityPersisters(entityMetadataProvider, dmlGenerator, jdbcTemplate);
        entityLoaders = new EntityLoaders(entityMetadataProvider, dmlGenerator, jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        metaModel = new MetaModelImpl(entityMetadataProvider, entityPersisters, entityLoaders);
    }

    @Test
    @DisplayName("getEntityMetadata 를 사용해 EntityMetadata 를 가져올 수 있다.")
    void getEntityMetadataTest() {
        final EntityMetadata<FixtureEntity.WithId> actual = metaModel.getEntityMetadata(FixtureEntity.WithId.class);
        final EntityMetadata<FixtureEntity.WithId> expect = EntityMetadata.from(FixtureEntity.WithId.class);

        assertThat(actual).isEqualTo(expect);
    }

    @Test
    @DisplayName("getOneToManyAssociatedEntitiesMetadata 를 사용해 해당 Entity 를 OneToMany 연관관계로 가진 EntityMetadata 들을 가져올 수 있다.")
    void getOneToManyAssociatedEntitiesMetadataTest() {
        final EntityMetadata<FixtureAssociatedEntity.WithId> targetEntityMetadata = EntityMetadata.from(FixtureAssociatedEntity.WithId.class);
        final Set<EntityMetadata<?>> oneToManyAssociatedEntitiesMetadata = metaModel.getOneToManyAssociatedEntitiesMetadata(targetEntityMetadata);

        assertThat(oneToManyAssociatedEntitiesMetadata).hasSize(6);
    }
}
