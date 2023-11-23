package persistence.entity.binder;


import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static persistence.entity.ClassScanner.scan;
import static persistence.entity.binder.AnnotationBinder.bindMetaModel;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityClassFilter;
import persistence.fake.FakeDialect;
import persistence.meta.MetaModel;
import persistence.testFixtures.Department;
import persistence.testFixtures.Employee;


@DisplayName("AnnotationBinder 테스트")
class AnnotationBinderTest {

    @Test
    @DisplayName("바인드 할 엔터티가 없으면 예외가 발생한다.")
    void noEntity() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> bindMetaModel(null, new FakeDialect()));
    }

    @Test
    @DisplayName("메타 모델을 생성한다.")
    void metaModel() {
        //given
        final Set<Class<?>> entityClass = EntityClassFilter.entityFilter(scan("persistence.testFixtures"));

        //when
        MetaModel metaModel = bindMetaModel(entityClass, new FakeDialect());

        //then
        assertSoftly((it) -> {
            it.assertThat(metaModel.getEntityMetaContext()).hasSize(7);
            it.assertThat(metaModel.getQueryGeneratorContext()).hasSize(7);
            it.assertThat(metaModel.getEntityMetaContext().get(Department.class)).isNotNull();
            it.assertThat(metaModel.getQueryGeneratorContext().get(Employee.class)).isNotNull();
        });
    }
}
