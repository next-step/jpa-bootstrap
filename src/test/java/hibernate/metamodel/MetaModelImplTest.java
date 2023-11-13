package hibernate.metamodel;

import hibernate.entity.meta.EntityClass;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MetaModelImplTest {

    @Test
    void 패키지하위의_Entity를_스캔하여_EntityClassMap을_생성한다() {
        MetaModel metaModel = MetaModelImpl.createPackageMetaModel("hibernate.metamodel.entity");
        Map<Class<?>, EntityClass<?>> actual = metaModel.getEntityClasses();
        assertThat(actual).hasSize(2);
    }
}
