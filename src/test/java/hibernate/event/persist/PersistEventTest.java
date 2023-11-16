package hibernate.event.persist;

import hibernate.metamodel.BasicMetaModel;
import hibernate.metamodel.MetaModel;
import hibernate.metamodel.MetaModelImpl;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PersistEventTest {

    private final MetaModel metamodel = MetaModelImpl.createPackageMetaModel(
            BasicMetaModel.createPackageMetaModel("hibernate.event.listener"),
            null
    );

    @Test
    void PersistEvent를_생성한다() {
        TestEntity givenEntity = new TestEntity();
        PersistEvent actual = PersistEvent.createEvent(metamodel, givenEntity);
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
