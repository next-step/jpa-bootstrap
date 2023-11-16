package hibernate.event.listener;

import hibernate.metamodel.BasicMetaModel;
import hibernate.metamodel.MetaModel;
import hibernate.metamodel.MetaModelImpl;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LoadEventTest {

    private final MetaModel metamodel = MetaModelImpl.createPackageMetaModel(
            BasicMetaModel.createPackageMetaModel("hibernate.event.listener"),
            null
    );

    @Test
    void LoadEvent를_생성한다() {
        LoadEvent actual = LoadEvent.createEvent(metamodel, TestEntity.class, 1L);
        assertAll(
                () -> assertThat(actual.getEntityId()).isEqualTo(1L),
                () -> assertThat(actual.getEntityLoader()).isNotNull()
        );
    }

    @Entity
    private static class TestEntity {
        @Id
        private Long id;
    }
}
