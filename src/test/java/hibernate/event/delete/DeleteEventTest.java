package hibernate.event.delete;

import hibernate.action.ActionQueue;
import hibernate.entity.EntityManagerImpl;
import hibernate.entity.EntitySource;
import hibernate.metamodel.BasicMetaModel;
import hibernate.metamodel.MetaModelImpl;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DeleteEventTest {

    private final EntitySource entitySource = new EntityManagerImpl(
            null,
            MetaModelImpl.createPackageMetaModel(BasicMetaModel.createPackageMetaModel("hibernate.event.delete"), null),
            null,
            new ActionQueue()
    );

    @Test
    void DeleteEvent를_생성한다() {
        TestEntity givenEntity = new TestEntity();
        DeleteEvent<TestEntity> actual = DeleteEvent.createEvent(entitySource, givenEntity);
        assertAll(
                () -> assertThat(actual.getEntity()).isEqualTo(givenEntity),
                () -> assertThat(actual.getEntityPersister()).isNotNull()
        );
    }

    @Entity
    private static class TestEntity {
        @Id
        private Long id;
    }
}
