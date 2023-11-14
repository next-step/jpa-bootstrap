package hibernate.metamodel;

import hibernate.entity.EntityLoader;
import hibernate.entity.EntityPersister;
import hibernate.entity.meta.EntityClass;
import hibernate.metamodel.entity.Entity1;
import hibernate.metamodel.entity.NoEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MetaModelImplTest {

    @Test
    void 패키지하위의_Entity를_스캔하여_EntityClass를_반환한다() {
        MetaModel metaModel = MetaModelImpl.createPackageMetaModel("hibernate.metamodel.entity", null);
        EntityClass<?> actual = metaModel.getEntityClass(Entity1.class);
        assertThat(actual).isNotNull();
    }

    @Test
    void Entity가_없는_Class의_EntityClass를_찾으려하는_경우_예외가_발생한다() {
        MetaModel metaModel = MetaModelImpl.createPackageMetaModel("hibernate.metamodel.entity", null);
        assertThatThrownBy(() -> metaModel.getEntityClass(NoEntity.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 클래스는 엔티티 클래스가 아닙니다.");
    }

    @Test
    void 패키지하위의_Entity를_스캔하여_EntityPersister를_반환한다() {
        MetaModel metaModel = MetaModelImpl.createPackageMetaModel("hibernate.metamodel.entity", null);
        EntityPersister<?> actual = metaModel.getEntityPersister(Entity1.class);
        assertThat(actual).isNotNull();
    }

    @Test
    void Entity가_없는_Class의_EntityPersister를_찾으려하는_경우_예외가_발생한다() {
        MetaModel metaModel = MetaModelImpl.createPackageMetaModel("hibernate.metamodel.entity", null);
        assertThatThrownBy(() -> metaModel.getEntityPersister(NoEntity.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 클래스는 엔티티 클래스가 아닙니다.");
    }

    @Test
    void 패키지하위의_Entity를_스캔하여_EntityLoader를_반환한다() {
        MetaModel metaModel = MetaModelImpl.createPackageMetaModel("hibernate.metamodel.entity", null);
        EntityLoader<?> actual = metaModel.getEntityLoader(Entity1.class);
        assertThat(actual).isNotNull();
    }

    @Test
    void Entity가_없는_Class의_EntityLoader를_찾으려하는_경우_예외가_발생한다() {
        MetaModel metaModel = MetaModelImpl.createPackageMetaModel("hibernate.metamodel.entity", null);
        assertThatThrownBy(() -> metaModel.getEntityLoader(NoEntity.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 클래스는 엔티티 클래스가 아닙니다.");
    }
}
