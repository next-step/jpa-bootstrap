package persistence.entity.database;

import database.dialect.Dialect;
import database.dialect.MySQLDialect;
import entity.Person5;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.bootstrap.Initializer;
import persistence.entity.EntityManager;
import persistence.entity.context.PersistentClass;
import testsupport.H2DatabaseTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static testsupport.EntityTestUtils.assertSamePerson;

class EntityLoaderTest extends H2DatabaseTest {
    private final Dialect dialect = MySQLDialect.getInstance();

    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        Initializer entity = new Initializer("entity", jdbcTemplate, dialect);
        entity.bootUp();
        entityManager = entity.newEntityManager();
    }

    @Test
    void loadMissingRecord() {
        PersistentClass<Person5> persistentClass = PersistentClass.from(Person5.class);
        EntityLoader<Person5> entityLoader = new EntityLoader<>(persistentClass, jdbcTemplate, dialect, List.of());

        assertThat(entityLoader.load(1L)).isEmpty();
    }

    @Test
    void load2() {
        PersistentClass<Person5> persistentClass = PersistentClass.from(Person5.class);
        EntityLoader<Person5> entityLoader = new EntityLoader<>(persistentClass, jdbcTemplate, dialect, List.of());

        Person5 person = new Person5(null, "abc123", 14, "c123@d.com");
        entityManager.persist(person);

        Person5 found = entityLoader.load(1L).get();

        assertSamePerson(found, person, false);
    }
}
