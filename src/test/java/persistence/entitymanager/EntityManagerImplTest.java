package persistence.entitymanager;

import app.entity.NoAutoIncrementUser;
import app.entity.Person5;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.EntityManagerFactory;
import persistence.bootstrap.Initializer;
import persistence.entity.database.PrimaryKeyMissingException;
import testsupport.H2DatabaseTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static testsupport.EntityTestUtils.*;

class EntityManagerImplTest extends H2DatabaseTest {
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        Initializer initializer = new Initializer("app.entity", jdbcTemplate, dialect);
        initializer.initialize();
        EntityManagerFactory entityManagerFactory = initializer.createEntityManagerFactory();
        entityManager = entityManagerFactory.openSession();
    }

    @Test
    void findMissingRecord() {
        assertThat(entityManager.find(Person5.class, 1L)).isNull();
    }

    @Test
    void persistAndFind() {
        Person5 person = newPerson(null, "abc123", 14, "c123@d.com");
        entityManager.persist(person);

        Person5 found = entityManager.find(Person5.class, 1L);

        assertSamePerson(found, person, false);
    }

    @Test
    void persistNoAutoIncrementEntityWithoutId() {
        NoAutoIncrementUser user = new NoAutoIncrementUser(null, "abc123", 14, "c123@d.com");

        PrimaryKeyMissingException ex = assertThrows(PrimaryKeyMissingException.class, () -> entityManager.persist(user));
        assertThat(ex.getMessage()).isEqualTo("Primary key is not assigned when inserting: app.entity.NoAutoIncrementUser");
    }

    @Test
    void persistNewRow() {
        Person5 person = newPerson(null, "abc123", 14, "c123@d.com");
        entityManager.persist(person);
        Person5 person2 = newPerson(null, "zzzzzz", 44, "zzzzz@d.com");
        entityManager.persist(person2);

        List<Person5> people = findPeople(jdbcTemplate);
        assertThat(people).hasSize(2);
        assertSamePerson(people.get(0), person, false);
        assertSamePerson(people.get(1), person2, false);
    }

    @Test
    void persistToUpdate() {
        Person5 person = newPerson(null, "abc123", 14, "c123@d.com");
        entityManager.persist(person);

        Person5 personToUpdate = newPerson(getLastSavedId(jdbcTemplate), "abc123", 15, "zzzzz@d.com");
        entityManager.persist(personToUpdate);
        entityManager.persist(personToUpdate);

        List<Person5> people = findPeople(jdbcTemplate);
        assertThat(people).hasSize(1);
        assertSamePerson(people.get(0), personToUpdate, true);
    }

    @Test
    void remove() {
        Person5 person = newPerson(null, "abc123", 14, "c123@d.com");
        entityManager.persist(person);

        Person5 person1 = entityManager.find(Person5.class, getLastSavedId(jdbcTemplate));
        entityManager.remove(person1);

        List<Person5> people = findPeople(jdbcTemplate);
        assertThat(people).hasSize(0);
    }
}
