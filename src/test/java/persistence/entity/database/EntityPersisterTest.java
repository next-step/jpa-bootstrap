package persistence.entity.database;

import app.entity.NoAutoIncrementUser;
import app.entity.Person5;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.EntityManagerFactory;
import persistence.bootstrap.Initializer;
import persistence.entitymanager.SessionContract;
import testsupport.H2DatabaseTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static testsupport.EntityTestUtils.*;

class EntityPersisterTest extends H2DatabaseTest {
    private SessionContract sessionContract;

    @BeforeEach
    void setUp() {
        Initializer initializer = new Initializer("app.entity", jdbcTemplate, dialect);
        initializer.initialize();
        EntityManagerFactory entityManagerFactory = initializer.createEntityManagerFactory();

        sessionContract = (SessionContract) entityManagerFactory.openSession();
    }

    @Test
    void insert() {
        EntityPersister<Person5> entityPersister = sessionContract.getEntityPersister(Person5.class);

        // row 두개를 추가하면
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
        EntityPersister<NoAutoIncrementUser> entityPersister = sessionContract.getEntityPersister(NoAutoIncrementUser.class);

        NoAutoIncrementUser person = new NoAutoIncrementUser(null, "some name", 11, "some@name.com");
        assertThrows(PrimaryKeyMissingException.class, () -> entityPersister.insert(person));
    }

    @Test
    void update() {
        EntityPersister<Person5> entityPersister = sessionContract.getEntityPersister(Person5.class);

        // row 한 개를 삽입하고,
        Person5 person = newPerson(null, "some name", 11, "some@name.com");
        entityPersister.insert(person);

        // 동일한 id 의 Person5 객체로 update 한 후
        Long savedId = getLastSavedId(jdbcTemplate);
        Person5 personUpdating = newPerson(savedId, "updated name", 20, "updated@email.com");

        entityPersister.updateEntity(savedId, personUpdating);

        // 남아있는 한개의 row 가 잘 업데이트돼야 한다
        List<Person5> people = findPeople(jdbcTemplate);
        assertThat(people).hasSize(1);
        Person5 found = people.get(0);
        assertSamePerson(found, personUpdating, true);
    }

    @Test
    void delete() {
        EntityPersister<Person5> entityPersister = sessionContract.getEntityPersister(Person5.class);

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
