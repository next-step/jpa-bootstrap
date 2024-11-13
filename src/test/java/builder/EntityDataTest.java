package builder;

import builder.dml.EntityData;
import builder.dml.EntityMetaData;
import builder.dml.EntityObjectData;
import entity.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityDataTest {

    @DisplayName("Class를 입력받아 EntityData를 생성한다.")
    @Test
    void createEntityDataInputClassTest() {
        EntityData entityData = new EntityData(new EntityMetaData(Person.class));
        assertThat(entityData.getEntityMetaData())
                .extracting("clazz", "tableName", "pkName", "alias")
                .containsExactly(Person.class, "users", "id", "users_");
    }

    @DisplayName("entityInstance 를 입력받아 EntityData를 생성한다.")
    @Test
    void createEntityDataInputEntityInstanceTest() {
        Person person = new Person(1L, "테스트", 10, "test@test.com");
        EntityData entityData = new EntityData(new EntityMetaData(person.getClass()), new EntityObjectData(person));

        assertAll(
                () -> assertThat(entityData.getEntityMetaData())
                        .extracting("clazz", "tableName", "pkName", "alias")
                        .containsExactly(Person.class, "users", "id", "users_"),
                () -> assertThat(entityData.getEntityObjectData())
                        .extracting("id", "entityInstance")
                        .containsExactly(1L, person)
        );
    }

    @DisplayName("Class와 key값을 입력받아 EntityData를 생성한다.")
    @Test
    void createEntityDataInputClassKeyTest() {
        Person person = new Person(1L, "테스트", 10, "test@test.com");
        EntityData entityData = new EntityData(new EntityMetaData(person.getClass()), new EntityObjectData(person));

        assertAll(
                () -> assertThat(entityData.getEntityMetaData())
                        .extracting("clazz", "tableName", "pkName", "alias")
                        .containsExactly(Person.class, "users", "id", "users_"),
                () -> assertThat(entityData.getEntityObjectData())
                        .extracting("id", "clazz")
                        .containsExactly(1L, Person.class)
        );
    }
}
