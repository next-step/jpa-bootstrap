package persistence.core;

import mock.MockPersistenceEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Application;

import static org.assertj.core.api.Assertions.assertThat;

class MetaModelFactoryTest {

    @Test
    @DisplayName("MetaModelFactory 를 통해 metaModel 를 생성할 수 있다.")
    void createMetaModelTest() {
        final MetaModelFactory metaModelFactory = new MetaModelFactory(new EntityScanner(Application.class), new MockPersistenceEnvironment());

        final MetaModel metaModel = metaModelFactory.createMetaModel();

        assertThat(metaModel).isNotNull();
    }
}
