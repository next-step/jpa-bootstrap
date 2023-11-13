package hibernate.binder;

import hibernate.binder.entity.Entity1;
import hibernate.binder.entity.Entity2;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationBinderTest {

    @Test
    void Entity_어노테이션이_달린_클래스를_가져온다() {
        List<Class<?>> actual = AnnotationBinder.parseEntityClasses("hibernate.binder");
        assertThat(actual).containsOnly(Entity1.class, Entity2.class, TestEntity.class);
    }

    @Test
    void Entity_어노테이션이_있는_클래스가_없는_경우_빈리스트를_반환한다() {
        List<Class<?>> actual = AnnotationBinder.parseEntityClasses("hibernate.binder.empty");
        assertThat(actual).isEmpty();
    }

    @Entity
    static class TestEntity {

        @Id
        private Long id;
    }
}
