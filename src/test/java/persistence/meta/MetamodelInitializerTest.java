package persistence.meta;

import domain.Order;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MetamodelInitializerTest {

    public static class TestPerson {
        private Long id;
        private String name;
        private Integer age;
        private String email;
        private Integer level;
    }

    @Entity
    public static class EntityPerson {
        @Id
        private Long id;
        private String name;
        private Integer age;
        private String email;
        private Integer level;
    }

    @Test
    @DisplayName("property의 package 이하의 Entity 어노테이션이 붙은 클래스를 찾아서 메타모델을 만든다.")
    void testReadEntity() {
        Metamodel metamodel = new MetamodelInitializer(null).getMetamodel();

        assertAll(
                () -> assertThat(metamodel.findTableDefinition(TestPerson.class)).isNull(),
                () -> assertThat(metamodel.findTableDefinition(EntityPerson.class)).isNotNull(),
                () -> assertThat(metamodel.findTableDefinition(Order.class)).isNull()
        );
    }
}
