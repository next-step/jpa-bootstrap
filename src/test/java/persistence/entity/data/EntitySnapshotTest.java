package persistence.entity.data;

import app.entity.Person5;
import database.sql.dml.part.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.Metadata;
import persistence.entity.context.PersistentClass;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static testsupport.EntityTestUtils.initializer;

class EntitySnapshotTest {
    private PersistentClass<Person5> persistentClass;

    @BeforeEach
    void setUp() {
        Metadata metadata = initializer(null).getMetadata();
        persistentClass = metadata.getPersistentClass(Person5.class);
    }

    @Test
    void getValueWithNullValues() {
        Person5 person = new Person5();
        EntitySnapshot oldEntitySnapshot = EntitySnapshot.of(persistentClass, null);
        EntitySnapshot entitySnapshot = EntitySnapshot.of(persistentClass, person);

        ValueMap changes = oldEntitySnapshot.diff(entitySnapshot);

        assertAll(
                () -> assertThat(changes.get("nick_name")).isNull(),
                () -> assertThat(changes.get("old")).isNull(),
                () -> assertThat(changes.get("email")).isNull()
        );
    }

    private final Person5 person0 = new Person5();
    private final Person5 person1 = new Person5(1L, "이름", 10, "이메일@a.com");
    private final Person5 person2 = new Person5(1L, "이름2", 20, "이메일@a.com");

    @Test
    void changes() {
        assertAll(
                () -> assertThat(changes(person0, person0)).isEqualTo(ValueMap.empty()),
                () -> assertThat(changes(person0, person1)).isEqualTo(ValueMap.of("nick_name", "이름", "old", 10, "email", "이메일@a.com")),
                () -> assertThat(changes(person0, person2)).isEqualTo(ValueMap.of("nick_name", "이름2", "old", 20, "email", "이메일@a.com")),
                () -> {
                    ValueMap map = ValueMap.of(
                            "nick_name", null,
                            "old", null,
                            "email", null
                    );
                    assertThat(changes(person1, person0)).isEqualTo(map);
                },
                () -> assertThat(changes(person1, person1)).isEqualTo(ValueMap.empty()),
                () -> assertThat(changes(person1, person2)).isEqualTo(ValueMap.of("nick_name", "이름2", "old", 20)),
                () -> {
                    ValueMap map = ValueMap.of(
                            "nick_name", null,
                            "old", null,
                            "email", null
                    );
                    assertThat(changes(person2, person0)).isEqualTo(map);
                },
                () -> assertThat(changes(person2, person1)).isEqualTo(ValueMap.of("nick_name", "이름", "old", 10)),
                () -> assertThat(changes(person2, person2)).isEqualTo(ValueMap.empty())

        );
    }

    private ValueMap changes(Person5 p1, Person5 p2) {
        return EntitySnapshot.of(persistentClass, p1).diff(EntitySnapshot.of(persistentClass, p2));
    }

}
