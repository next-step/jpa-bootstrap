package hibernate.metamodel;

import hibernate.entity.meta.EntityClass;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BasicMetaModelTest {

    @Test
    void 패키지하위의_Entity를_스캔하여_EntityClass를_반환한다() {
        BasicMetaModel basicMetaModel = BasicMetaModel.createPackageMetaModel("hibernate.metamodel.entity");
        Map<Class<?>, EntityClass<?>> actual = basicMetaModel.getEntityClassMap();
        assertThat(actual).hasSize(2);
    }
}
