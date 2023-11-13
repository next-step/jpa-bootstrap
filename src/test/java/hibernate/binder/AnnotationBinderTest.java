package hibernate.binder;

import hibernate.binder.entity.Entity1;
import hibernate.binder.entity.Entity2;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationBinderTest {

    @Test
    void Entity_어노테이션이_달린_클래스를_가져온다() throws IOException, ClassNotFoundException {
        List<Class<?>> actual = AnnotationBinder.parseEntityClasses("hibernate.binder");
        assertThat(actual).containsOnly(Entity1.class, Entity2.class, TestEntity.class);
    }

    @Entity
    static class TestEntity {

        @Id
        private Long id;
    }
}
