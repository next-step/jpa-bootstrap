package hibernate;

import entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationBinderTest {

    @DisplayName("entity패키지에 @Entity 어노테이션을 가진 클래스를 가져온다.")
    @Test
    void getEntityTest() {
        AnnotationBinder annotationBinder = new AnnotationBinder("entity");

        assertThat(annotationBinder.getEntityClasses())
                .contains(Order.class, OrderItem.class, OrderLazy.class, Person.class, Department.class, Employee.class);
    }

}
