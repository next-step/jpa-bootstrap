package hibernate.event.merge;

import hibernate.action.ActionQueue;
import hibernate.entity.EntityManagerImpl;
import hibernate.entity.EntitySource;
import hibernate.metamodel.BasicMetaModel;
import hibernate.metamodel.MetaModelImpl;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MergeEventTest {

    private final EntitySource entitySource = new EntityManagerImpl(
            null,
            MetaModelImpl.createPackageMetaModel(BasicMetaModel.createPackageMetaModel("hibernate.event.merge"),null),
            null,
            new ActionQueue()
    );

    @Test
    void MergeEvent를_생성한다() {
        MergeEvent<TestEntity> actual = MergeEvent.createEvent(entitySource, TestEntity.class, 1L, Map.of());
        assertAll(
                () -> assertThat(actual.getEntityPersister()).isNotNull(),
                () -> assertThat(actual.getEntityId()).isEqualTo(1L),
                () -> assertThat(actual.getChangeColumns()).isNotNull()
        );
    }

    @Entity
    private static class TestEntity {
        @Id
        private Long id;

        private String name;
    }
}
