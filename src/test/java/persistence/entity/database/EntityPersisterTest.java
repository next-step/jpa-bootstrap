package persistence.entity.database;

import entity.NoAutoIncrementUser;
import entity.Person5;
import org.junit.jupiter.api.Test;
import testsupport.H2DatabaseTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static testsupport.EntityTestUtils.*;

class EntityPersisterTest extends H2DatabaseTest {
    @Test
    void insert() {
        // row 두개를 추가하면
        EntityPersister<Person5> entityPersister = new EntityPersister<>(Person5.class, jdbcTemplate);

        Person5 person = newPerson(null, "some name", 11, "some@name.com");
        entityPersister.insert(person);
        Person5 person2 = newPerson(null, "another name", 22, "another@name.com");
        entityPersister.insert(person2);

        // 잘 들어가있어야 한다
        List<Person5> people = findPeople(jdbcTemplate);
        assertSamePerson(people.get(0), person, false);
        assertThat(people.get(0).getId()).isNotZero();
        assertSamePerson(people.get(1), person2, false);
        assertThat(people.get(1).getId()).isNotZero();
    }

    @Test
    void insertIntoEntityWithNoIdGenerationStrategy() {
        EntityPersister<NoAutoIncrementUser> entityPersister = new EntityPersister(NoAutoIncrementUser.class, jdbcTemplate);

        NoAutoIncrementUser person = new NoAutoIncrementUser(null, "some name", 11, "some@name.com");
        assertThrows(PrimaryKeyMissingException.class, () -> entityPersister.insert(person));
    }

    @Test
    void update() {
        EntityPersister<Person5> entityPersister = new EntityPersister<>(Person5.class, jdbcTemplate);
        // row 한 개를 삽입하고,
        Person5 person = newPerson(null, "some name", 11, "some@name.com");
        entityPersister.insert(person);

        // 동일한 id 의 Person5 객체로 update 한 후
        Long savedId = getLastSavedId(jdbcTemplate);
        Person5 personUpdating = newPerson(savedId, "updated name", 20, "updated@email.com");

        entityPersister.update(savedId, personUpdating);

        // 남아있는 한개의 row 가 잘 업데이트돼야 한다
        List<Person5> people = findPeople(jdbcTemplate);
        assertThat(people).hasSize(1);
        Person5 found = people.get(0);
        assertSamePerson(found, personUpdating, true);
    }

    @Test
    void delete() {
        EntityPersister<Person5> entityPersister = new EntityPersister<>(Person5.class, jdbcTemplate);
        // row 한 개를 저장 후에
        Person5 person = newPerson(null, "some name", 11, "some@name.com");
        entityPersister.insert(person);

        // 그 row 를 삭제하고
        Long savedId = getLastSavedId(jdbcTemplate);
        entityPersister.delete(savedId);

        // 개수를 세면 0개여야 한다.
        List<Person5> people = findPeople(jdbcTemplate);
        assertThat(people).hasSize(0);
    }
}
